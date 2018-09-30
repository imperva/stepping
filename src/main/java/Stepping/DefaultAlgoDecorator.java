package Stepping;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

public class DefaultAlgoDecorator extends IAlgoDecorator implements IExceptionHandler {
    private Container cntr = new Container();
    private IMessenger iMessenger;
    private Algo algo;
    private Running running;
    private boolean isClosed = false;

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

        decorateSteps();
        initSteps();
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
        DI(this, DefaultID.EXCEPTION_HANDLER.name());
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
    public StepConfig getStepConfig() {
        return algo.getStepConfig();
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
        try {
            if(isClosed)
                return;
            List<Closeable> closeables = new ArrayList<>();
            closeables.addAll(getContainer().getSonOf(IStepDecorator.class));
            closeables.addAll(getContainer().getSonOf(Running.class));
            for (Closeable closable : closeables) {
                try {
                    closable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            this.running.close();
            this.iMessenger.close();
        }catch (Exception e){

        }finally {
            isClosed=true;
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
        for (IStepDecorator step : cntr.<IStepDecorator>getSonOf(IStepDecorator.class)) {
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
        int delay = new Integer(SteppingProperties.getInstance().getProperty("stepping.default.step.delay"));
        int initialDelay = new Integer(SteppingProperties.getInstance().getProperty("stepping.default.step.initialdelay"));
        boolean isDaemon = new Boolean(SteppingProperties.getInstance().getProperty("stepping.default.step.daemon"));
        for (IStepDecorator iStepDecorator : cntr.<IStepDecorator>getSonOf(IStepDecorator.class)) {
            cntr.add(new Running(iStepDecorator.getStep().getClass().getName(),
                    iStepDecorator,
                    delay,
                    initialDelay,
                    isDaemon));
        }
    }


    private void initSteps() {
        for (IStepDecorator step : cntr.<IStepDecorator>getSonOf(IStepDecorator.class)) {
            step.init();
            step.setContainer(cntr);
            step.setStepConfig(getStepConfig());
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
        tickCallBack();
    }

    @Override
    public void handle(Exception e) {
        System.out.println("Error: " + e.toString());
        close();
    }
}