package stepping;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

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
            fillContainer();
            decorateSteps();
            duplicateNodes();
            initSteps();
            initRunners();
            initSubjectContainer();
            registerShutdownHook();
            attachSubjects();
            restate();

            wakenRunners();
        } catch (Exception e) {

            System.out.println(e);
            close();
        }
    }

    private void fillContainer() {
        ContainerRegistrar builtinRegistration = builtinContainerRegistration();

        ContainerRegistrar objectsRegistration = containerRegistration();

        DI(builtinRegistration.getRegistered());
        DI(objectsRegistration.getRegistered());


        if (!cntr.exist(BuiltinTypes.STEPPING_EXCEPTION_HANDLER.name()))
            DI(this, BuiltinTypes.STEPPING_EXCEPTION_HANDLER.name());
    }

    private void decorateSteps() {
        for (Step step : cntr.<Step>getSonOf(Step.class)) {
            DefaultStepDecorator defaultStepDecorator = new DefaultStepDecorator(step);
            cntr.add(defaultStepDecorator);
        }
    }

    private void duplicateNodes(){
        for (IStepDecorator iStepDecorator : cntr.<IStepDecorator>getSonOf(IStepDecorator.class)) {
            int numOfNodes = iStepDecorator.getConfig().getNumOfNodes();
            if (numOfNodes > 0) {
                for (int i = 1; i <= numOfNodes - 1; i++) {
                    DefaultStepDecorator defaultStepDecorator = new DefaultStepDecorator(iStepDecorator.getStep());
                    defaultStepDecorator.setDistributionNodeID(defaultStepDecorator.getClass().getName());
                    cntr.add(defaultStepDecorator);
                }
            }
        }
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    private void wakenRunners() {
        for (IRunning running : cntr.<IRunning>getSonOf(IRunning.class)) {
            running.awake();
        }
        if (running != null)
            running.awake();
    }

    private ContainerRegistrar builtinContainerRegistration() {
        ContainerRegistrar containerRegistrar = new ContainerRegistrar();
        SubjectContainer subjectContainer = new SubjectContainer();
        containerRegistrar.add(BuiltinTypes.STEPPING_SUBJECT_CONTAINER.name(), subjectContainer);
        containerRegistrar.add(BuiltinTypes.STEPPING_SHOUTER.name(), new Shouter(subjectContainer));

        containerRegistrar.add(BuiltinSubjectType.STEPPING_DATA_ARRIVED.name(), new Subject(BuiltinSubjectType.STEPPING_DATA_ARRIVED.name()));
        containerRegistrar.add(BuiltinSubjectType.STEPPING_PUBLISH_DATA.name(), new Subject(BuiltinSubjectType.STEPPING_PUBLISH_DATA.name()));
        return containerRegistrar;
    }

    @Override
    public void onTickCallBack() {
        algo.onTickCallBack();
    }

    @Override
    public AlgoConfig getConfig() {
        return algo.getConfig();
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

    private void initSubjectContainer() {
        SubjectContainer subjectContainer = getSubjectContainer();
        for (Subject subject : getContainer().<Subject>getTypeOf(Subject.class)) {
            subjectContainer.add(subject);
        }
    }

    private void restate() {
        List<Thread> threads = new ArrayList<>();
        for (IStepDecorator step : cntr.<IStepDecorator>getSonOf(IStepDecorator.class)) {
            Thread thread = new Thread(() -> {
                step.onRestate();
            });
            thread.setName("onRestate: " + step.getClass().getName());
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

    private void initRunners() {
        AlgoConfig globConf = getConfig();
        for (IStepDecorator iStepDecorator : cntr.<IStepDecorator>getSonOf(IStepDecorator.class)) {

            String runnerID = iStepDecorator.getStep().getClass().getName();

            if (iStepDecorator.getConfig().isEnableTickCallback()) {
                int delay = iStepDecorator.getStep().getConfig() != null ? iStepDecorator.getStep().getConfig().getRunningPeriodicDelay() : globConf.getRunningPeriodicDelay();
                int initialDelay = iStepDecorator.getStep().getConfig() != null ? iStepDecorator.getStep().getConfig().getRunningInitialDelay() : globConf.getRunningInitialDelay();
                CyclicBarrier cb = new CyclicBarrier(2);
                TimeUnit timeUnit = iStepDecorator.getConfig().getRunningPeriodicDelayUnit();
                cntr.add(new RunningScheduled(runnerID + "tickcallback", delay, initialDelay, timeUnit,
                        () -> {
                            try {
                                iStepDecorator.queueSubjectUpdate(new Data(cb), BuiltinSubjectType.STEPPING_TIMEOUT_CALLBACK.name());
                                cb.await();
                            } catch (Exception e) {
                                handle(e);
                            }
                        }));
            }

            cntr.add(new Running(runnerID, iStepDecorator::openDataSink));
        }

        if (this.getConfig().isEnableTickCallback()) {
            this.running = new RunningScheduled(DefaultAlgoDecorator.class.getName(),
                    globConf.getRunningPeriodicDelay(),
                    globConf.getRunningInitialDelay(),
                    TimeUnit.MILLISECONDS,
                    this::onTickCallBack);
        }
    }

    private void initSteps() {
        for (IStepDecorator step : cntr.<IStepDecorator>getSonOf(IStepDecorator.class)) {
            step.init(cntr);
            step.setAlgoConfig(getConfig());
        }
    }

    private void attachSubjects() {
        SubjectContainer subjectContainer = getContainer().getById(BuiltinTypes.STEPPING_SUBJECT_CONTAINER.name());

        for (IStepDecorator iStepDecorator : cntr.<IStepDecorator>getSonOf(IStepDecorator.class)) {
            for (ISubject subject : subjectContainer.getSubjectsList()) {
                iStepDecorator.followSubject(subject);
            }
        }
    }

    protected SubjectContainer getSubjectContainer() {
        return getContainer().getById(BuiltinTypes.STEPPING_SUBJECT_CONTAINER.name());
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