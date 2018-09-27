package Stepping;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

public class DefaultAlgoDecorator extends IAlgoDecorator {
    private Container cntr = new Container();
    private IMessenger iMessenger;
    private Algo algo;
    private Running running;

    protected DefaultAlgoDecorator(Algo algo) {
        this.algo = algo;
        this.running = new Running(DefaultAlgoDecorator.class.getName(), this,
                new Integer(SteppingProperties.getInstance().getProperty("stepping.default.algo.delay")),
                new Integer(SteppingProperties.getInstance().getProperty("stepping.default.algo.initialdelay")),
                new Boolean(SteppingProperties.getInstance().getProperty("stepping.default.algo.daemon")));
    }

    @Override
    public void init() {
        registerIoC();

        initSteps();
        decorateSteps();
        makeStepDecoratorRun();

        initSubjects();

        registerShutdownHook();
        attachSubjects();

        restate();

        wakenAllProcessingUnit();
    }

    private void registerIoC() {
        HashMap<String, Object> defaultIoCMap = DefaultIoC();
        HashMap<String, Object> IoCMap = IoC();
        defaultIoCMap.putAll(IoCMap);
        DI(defaultIoCMap);
    }

    private void decorateSteps() {
        for (Step step : cntr.<Step>getSonOf(Step.class)) {
            cntr.add(new DefaultStepDecorator(step));
        }
    }


    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    private void wakenAllProcessingUnit() {
        for (Running running : cntr.<Running>getSonOf(Running.class)) {
            running.wakenProcessingUnit();
        }
        running.wakenProcessingUnit();
    }


    //todo Add abstract method IoC that each child will need to implement
    //todo Add iMessenger to ExternalDataConsumerDefaultStep and remove it from all steps

    private HashMap<String, Object> DefaultIoC() {
        HashMap<String, Object> objectHashMap = new HashMap<>();
        objectHashMap.put(DefaultID.SUBJECT_CONTAINER.name(), new SubjectContainer());

        objectHashMap.put(DefaultSubjectType.S_DATA_ARRIVED.name(), new Subject(DefaultSubjectType.S_DATA_ARRIVED.name()));
        objectHashMap.put(DefaultSubjectType.S_PUBLISH_DATA.name(), new Subject(DefaultSubjectType.S_PUBLISH_DATA.name()));
        if (iMessenger != null) {
            ExternalDataConsumerDefaultStep externalDataConsumerStep = new ExternalDataConsumerDefaultStep();
            externalDataConsumerStep.setMessenger(iMessenger);
            ExternalDataProducerDefaultStep externalDataProducerStep = new ExternalDataProducerDefaultStep();
            externalDataProducerStep.setMessenger(iMessenger);
            objectHashMap.put(DefaultID.EXTERNAL_DATA_CONSUMER.name(), externalDataConsumerStep);
            objectHashMap.put(DefaultID.EXTERNAL_DATA_PRODUCER.name(), externalDataProducerStep);
        }
        return objectHashMap;
    }

    @Override
    public void tickCallBack() {
        algo.tickCallBack();
    }

    @Override
    public void setMessenger(IMessenger messenger) {
        algo.setMessenger(iMessenger);
        this.iMessenger = messenger;
    }

    @Override
    public HashMap<String, Object> IoC() {
        return algo.IoC();
    }

    @Override
    public void close() {
        //todo if messanger is in our container then it will be closed as all others. thus shotdown is redundunt
        if (iMessenger != null) {
            iMessenger.shutdown();
        }

        for (Closeable closable : getContainer().<Closeable>getSonOf(Closeable.class)) {
            try {
                closable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initSubjects() {
        for (Subject subject : getContainer().<Subject>getTypeOf(Subject.class)) {
            SubjectContainer subjectContainer = getSubjectContainer();
            subjectContainer.add(subject);
            subject.setContainer(cntr);
        }
    }

    private void restate() {
        List<Thread> threads = new ArrayList<>();
        for (Step step : cntr.<Step>getSonOf(Step.class)) {
            Thread thread = new Thread(() -> {
                step.restate();
            });
            thread.setName("restate: " + step.getClass().getName());
            thread.start();
            threads.add(thread);
        }

        threads.forEach((t) -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void makeStepDecoratorRun() {
        for (IStepDecorator iStepDecorator : cntr.<IStepDecorator>getSonOf(IStepDecorator.class)) {
            cntr.add(new Running(iStepDecorator.getStep().getClass().getName(),
                    iStepDecorator,
                    new Integer(SteppingProperties.getInstance().getProperty("stepping.default.step.delay")),
                    new Integer(SteppingProperties.getInstance().getProperty("stepping.default.step.initialdelay")),
                    new Boolean(SteppingProperties.getInstance().getProperty("stepping.default.step.daemon"))));
        }
    }


    private void initSteps() {
        for (Step step : cntr.<Step>getSonOf(Step.class)) {
            step.init();
            step.setContainer(cntr);
        }
    }

    private void attachSubjects() {
        SubjectContainer subjectContainer = getContainer().getById(DefaultID.SUBJECT_CONTAINER.name());

        for (IStepDecorator iStepDecorator : cntr.<IStepDecorator>getSonOf(IStepDecorator.class)) {
            for (ISubject subject : subjectContainer.getSubjectsList()) {
                iStepDecorator.attach(subject);
            }
        }
    }

    protected SubjectContainer getSubjectContainer() {
        return getContainer().getById(DefaultID.SUBJECT_CONTAINER.name());
    }

    protected Container getContainer() {
        return cntr;
    }

    private <T> void DI(T obj, String id) {
        cntr.add(obj, id);
    }

    private void DI(HashMap<String, Object> objs) {
        objs.forEach((s, o) -> DI(o, s));
    }

    @Override
    public void run() {
        algo.tickCallBack();
    }
}