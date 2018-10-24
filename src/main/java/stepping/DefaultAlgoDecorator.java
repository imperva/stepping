package stepping;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

public class DefaultAlgoDecorator implements IExceptionHandler, IAlgoDecorator {
    private Container cntr = new Container();

    private Algo algo;
    private IRunning running;
    private boolean isClosed = false;

    DefaultAlgoDecorator(Algo algo) {
        this.algo = algo;
    }

    @Override
    public void init() {
        try {
            registerIoC();
            decorateSteps();
            duplicateNodes();
            initSteps();
            initRunning();
            initSubjects();
            registerShutdownHook();
            attachSubjects();
            restate();

            wakenRunningUnits();
        } catch (Exception e) {

            System.out.println(e);
            close();
        }
    }

    private void registerIoC() {
        ContainerRegistrar builtinRegistration = builtinContainerRegistration();

        ContainerRegistrar objectsRegistration = containerRegistration();

        DI(builtinRegistration.getRegistered());
        DI(objectsRegistration.getRegistered());

        if (!cntr.exist(DefaultIoCID.STEPPING_EXCEPTION_HANDLER.name()))
            DI(this, DefaultIoCID.STEPPING_EXCEPTION_HANDLER.name());
    }

    private void decorateSteps() {
        for (Step step : cntr.<Step>getSonOf(Step.class)) {
            DefaultStepDecorator defaultStepDecorator = new DefaultStepDecorator(step);
            cntr.add(defaultStepDecorator);
        }
    }

    private void duplicateNodes(){
        for (IStepDecorator iStepDecorator : cntr.<IStepDecorator>getSonOf(IStepDecorator.class)) {
            int numOfNodes = iStepDecorator.getLocalStepConfig().getNumOfNodes();
            if (numOfNodes > 0) {
                for (int i = 1; i <= numOfNodes - 1; i++) {
                    cntr.add(new DefaultStepDecorator(iStepDecorator));
                }
            }
        }
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    private void wakenRunningUnits() {
        for (IRunning running : cntr.<IRunning>getSonOf(IRunning.class)) {
            running.awake();
        }
        if (running != null)
            running.awake();
    }

    private ContainerRegistrar builtinContainerRegistration() {
        ContainerRegistrar containerRegistrar = new ContainerRegistrar();
        containerRegistrar.add(DefaultIoCID.STEPPING_SUBJECT_CONTAINER.name(), new SubjectContainer());

        containerRegistrar.add(DefaultSubjectType.STEPPING_DATA_ARRIVED.name(), new Subject(DefaultSubjectType.STEPPING_DATA_ARRIVED.name()));
        containerRegistrar.add(DefaultSubjectType.STEPPING_PUBLISH_DATA.name(), new Subject(DefaultSubjectType.STEPPING_PUBLISH_DATA.name()));

        return containerRegistrar;
    }

    @Override
    public void tickCallBack() {
        algo.tickCallBack();
    }

    @Override
    public GlobalAlgoStepConfig getGlobalAlgoStepConfig() {
        return algo.getGlobalAlgoStepConfig();
    }


    @Override
    public ContainerRegistrar containerRegistration() {
        return algo.containerRegistration();
    }

    @Override
    public void close() {
        try {
            if (isClosed)
                return;
            List<Closeable> closeables = new ArrayList<>();
            closeables.addAll(getContainer().getSonOf(IStepDecorator.class));
            closeables.addAll(getContainer().getSonOf(IRunning.class));
            for (Closeable closable : closeables) {
                try {
                    closable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            this.running.close();
        } catch (Exception e) {

        } finally {
            isClosed = true;
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

    private void initRunning() {
        GlobalAlgoStepConfig globConf = getGlobalAlgoStepConfig();
        for (IStepDecorator iStepDecorator : cntr.<IStepDecorator>getSonOf(IStepDecorator.class)) {
            int delay = iStepDecorator.getStep().getLocalStepConfig() != null ? iStepDecorator.getStep().getLocalStepConfig().getRunningPeriodicDelay() : globConf.getRunningPeriodicDelay();
            int initialDelay = iStepDecorator.getStep().getLocalStepConfig() != null ? iStepDecorator.getStep().getLocalStepConfig().getRunningInitialDelay() : globConf.getRunningInitialDelay();

            if(iStepDecorator.getLocalStepConfig().isEnableTickCallback()) {
                cntr.add(new IRunningScheduled(iStepDecorator.getStep().getClass().getName(),
                        delay,
                        initialDelay,
                        () -> {
                            iStepDecorator.tickCallBack();
                        }));
            }

            cntr.add(new Running(iStepDecorator.getStep().getClass().getName(),
                    () -> {
                        iStepDecorator.dataListener();
                    }));


        }

        if(this.getGlobalAlgoStepConfig().isEnableTickCallback()) {
            this.running = new IRunningScheduled(DefaultAlgoDecorator.class.getName(),
                    globConf.getRunningPeriodicDelay(),
                    globConf.getRunningInitialDelay(),
                    () -> {
                        this.tickCallBack();
                    });
        }
    }

    private void initSteps() {
        for (IStepDecorator step : cntr.<IStepDecorator>getSonOf(IStepDecorator.class)) {
            step.init(cntr);
            step.setGlobalAlgoStepConfig(getGlobalAlgoStepConfig());
        }
    }

    private void attachSubjects() {
        SubjectContainer subjectContainer = getContainer().getById(DefaultIoCID.STEPPING_SUBJECT_CONTAINER.name());

        for (IStepDecorator iStepDecorator : cntr.<IStepDecorator>getSonOf(IStepDecorator.class)) {
            for (ISubject subject : subjectContainer.getSubjectsList()) {
                iStepDecorator.attach(subject);
            }
        }
    }

    protected SubjectContainer getSubjectContainer() {
        return getContainer().getById(DefaultIoCID.STEPPING_SUBJECT_CONTAINER.name());
    }

    protected Container getContainer() {
        return cntr;
    }

    private <T> void DI(T obj, String id) {
        cntr.add(obj, id);
    }

    private <T> void DI(T obj) {
        cntr.add(obj);
    }

    private void DI(HashMap<String, Object> objs) {
        objs.forEach((s, o) -> DI(o, s));
    }


    @Override
    public void handle(Exception e) {
        System.out.println("Error: " + e.toString());
        close();
    }
}