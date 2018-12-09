package stepping;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class AlgoDecorator implements IBuiltinExceptionHandler, IAlgoDecorator {
    static final Logger logger = LoggerFactory.getLogger(AlgoDecorator.class);

    private volatile Container cntr = new Container();
    private Algo algo;
    private IRunning running;
    private boolean isClosed = false;
    private final Object closingLock = new Object();

    AlgoDecorator(Algo algo) {
        this.algo = algo;
    }

    @Override
    public void init() {
        try {
            logger.info("Initializing Algo...");
            logger.info("Populating container...");
            fillContainer();
            logger.info("Decorating Steps...");
            decorateSteps();
            logger.info("Duplicating Parallel Nodes Steps...");
            duplicateNodes();
            logger.info("Initializing Steps...");
            initSteps();
            logger.info("Initializing Runners...");
            initRunners();
            logger.info("Initializing Subjects...");
            initSubjectContainer();
            logger.info("Register ShutdownHook...");
            registerShutdownHook();
            logger.info("Attach Subjects to Followers...");
            attachSubjects();
            logger.info("Starting Restate stage...");
            restate();

            logger.debug("Run Steps...");
            wakenRunners();


            algo.init();
        } catch (Exception e) {
            logger.error("Algo initialization FAILED", e);
            handle(e);
        }
    }

    private void fillContainer() {
        ContainerRegistrar builtinRegistration = builtinContainerRegistration();

        ContainerRegistrar objectsRegistration = containerRegistration();

        DI(builtinRegistration.getRegistered());
        DI(objectsRegistration.getRegistered());
    }

    private void decorateSteps() {
        for (Step step : cntr.<Step>getSonOf(Step.class)) {
            StepDecorator stepDecorator = new StepDecorator(step);
            cntr.add(stepDecorator);
        }
    }

    private void duplicateNodes() {
        for (IStepDecorator iStepDecorator : cntr.<IStepDecorator>getSonOf(IStepDecorator.class)) {
            int numOfNodes = iStepDecorator.getConfig().getNumOfNodes();
            if (numOfNodes > 0) {
                for (int i = 1; i <= numOfNodes - 1; i++) {
                    StepDecorator stepDecorator = new StepDecorator(iStepDecorator.getStep());
                    stepDecorator.setDistributionNodeID(stepDecorator.getClass().getName());
                    cntr.add(stepDecorator);
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
        containerRegistrar.add(BuiltinTypes.STEPPING_SHOUTER.name(), new Shouter(subjectContainer, this));

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
            thread.setUncaughtExceptionHandler((t, e) -> {
                if (e instanceof Exception) {
                    Exception ex = (Exception) e;
                    logger.error("OnRestate phase FAILED", ex);
                    handle(ex);
                    System.exit(1);
                }
            });
            thread.start();
            threads.add(thread);
        }

        threads.forEach((t) -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                logger.error("Exception while waiting for restate phase to complete");
            }

        });
    }

    private void initRunners() {
        AlgoConfig globConf = getConfig();
        for (IStepDecorator iStepDecorator : cntr.<IStepDecorator>getSonOf(IStepDecorator.class)) {


            if (iStepDecorator.getConfig().isEnableTickCallback()) {
                long delay = iStepDecorator.getStep().getConfig() != null ? iStepDecorator.getStep().getConfig().getRunningPeriodicDelay() : globConf.getRunningPeriodicDelay();
                long initialDelay = iStepDecorator.getStep().getConfig() != null ? iStepDecorator.getStep().getConfig().getRunningInitialDelay() : globConf.getRunningInitialDelay();
                CyclicBarrier cb = new CyclicBarrier(2);
                TimeUnit timeUnit = iStepDecorator.getConfig().getRunningPeriodicDelayUnit();
                cntr.add(new RunningScheduled("Step TickCallBack - " + iStepDecorator.getStep().getClass().getName(), delay, initialDelay, timeUnit,
                        () -> {
                            try {
                                iStepDecorator.queueSubjectUpdate(new Data(cb), BuiltinSubjectType.STEPPING_TIMEOUT_CALLBACK.name());
                                cb.await();
                            } catch (Exception e) {
                                handle(e);
                            }
                        }));
            }

            String runnerID = "Step DataSink - " + iStepDecorator.getStep().getClass().getName();
            cntr.add(new Running(runnerID, () -> {
                try {
                    iStepDecorator.openDataSink();
                } catch (Exception e) {
                    handle(e);
                }
            }));
        }

        if (this.getConfig().isEnableTickCallback()) {
            this.running = new RunningScheduled("Algo TickCallback - " + algo.getClass().getName(),
                    globConf.getRunningPeriodicDelay(),
                    globConf.getRunningInitialDelay(),
                    TimeUnit.MILLISECONDS,
                    () -> {
                        try {
                            onTickCallBack();
                        } catch (Exception e) {
                            handle(e);
                        }
                    });
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
    public boolean handle(Exception e) {
        synchronized (closingLock) {
            if (isClosed)
                return true;
            String error = "Exception Detected";
            logger.error(error, e);
            close();

        }
        return true;
    }

    @Override
    public void handle(SteppingException e) {
        synchronized (closingLock) {
            if (isClosed)
                return;
            String error = "Exception Detected in Step - " + e.getStepId();
            logger.error(error, e);
            close();
        }
    }

    @Override
    public void handle(SteppingSystemException e) {
        synchronized (closingLock) {
            if (isClosed)
                return;
            String error = "Exception Detected";
            if (e instanceof SteppingDistributionException)
                error += " while distributing Subject - " + ((SteppingDistributionException) e).getSubjectType();
            logger.error(error, e);
            close();
        }
    }

    public void close() {
        synchronized (closingLock) {
            try {
                if (isClosed)
                    return;
                List<Closeable> closeables = getContainer().getSonOf(IStepDecorator.class);
                for (Closeable closable : closeables) {
                    try {
                        closable.close();
                    } catch (IOException e) {
                        logger.error("Failed to close a closeable object, continuing with the next one");
                    }
                }
            } finally {
                isClosed = true;
                IRunning.kill();
            }
        }
    }


    /*@Override
    public void close() {
        try {
            logger.info("Try close entire Algo players");
            //if (isClosed) {
            //    logger.debug("Algo already closed");
             //   return;
            //}

            List<Closeable> closeables = new ArrayList<>();
            closeables.addAll(getContainer().getSonOf(IStepDecorator.class));
            closeables.addAll(getContainer().getSonOf(IRunning.class));
            logger.debug(closeables.size() + " closeables found");
            for (Closeable closable : closeables) {
                try {
                    closable.close();
                } catch (IOException e) {
                    logger.error("Failed to close a closeable object, continuing with the next one");
                }
            }

        } catch (Exception e) {
            logger.error("Failed to close Algo", e);
        } finally {
            //isClosed = true;
            try {
                IRunning.kill();
                this.running.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }*/
}