package com.imperva.stepping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Closeable;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

class AlgoDecorator implements IBuiltinExceptionHandler, IAlgoDecorator {
    private final Logger logger = LoggerFactory.getLogger(AlgoDecorator.class);
    private Container cntr = new Container();
    private Algo algo;
    private IRunning runningAlgoTickCallback;
    private RunnersController runnersController = new RunnersController();//* todo Use CompletionService
    private volatile boolean isClosed = false;
    private final Object closingLock = new Object();
    private Future runningAlgoTickCallbackFuture;
    private SteppingConfig steppingConfig;

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
            logger.info("Fill Subjects in Container...");
            fillAutoCreatedSubjectsInContainer();
            logger.info("Duplicating Parallel Nodes Steps...");
            duplicateNodes();
            logger.info("Initializing Steps...");
            initSteps();
            logger.info("Initializing Runners...");
            initRunners();
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

    private void fillAutoCreatedSubjectsInContainer() {
        ContainerRegistrar autoSubjectsRegistration = autoSubjectsRegistration();
        DI(autoSubjectsRegistration.getRegistered());
    }

    private void fillContainer() {
        ContainerRegistrar builtinRegistration = builtinContainerRegistration();
        ContainerRegistrar objectsRegistration = containerRegistration();

        DI(builtinRegistration.getRegistered());
        DI(objectsRegistration.getRegistered());
    }

    private ContainerRegistrar autoSubjectsRegistration() {
        ContainerRegistrar containerRegistrar = new ContainerRegistrar();
        for (IStepDecorator step : cntr.<IStepDecorator>getSonOf(IStepDecorator.class)) {
            Follower follower = step.listSubjectsToFollow();
            if (follower != null && follower.size() != 0) {
                for (String subjectType : follower.get()) {
                    ISubject s = cntr.getById(subjectType);
                    if (s == null) { //* If exist do nothing
                        s = new Subject(subjectType);
                        containerRegistrar.add(subjectType, s);
                    }

                }
            }
        }
        return containerRegistrar;
    }

    private void decorateSteps() {
        for (Step step : cntr.<Step>getSonOf(Step.class)) {
            StepDecorator stepDecorator = new StepDecorator(step);
            String decoratorId = step.getId() + "_" + "decorator";
            stepDecorator.setId(decoratorId);
            cntr.add(stepDecorator, decoratorId);
        }
    }

    private void duplicateNodes() {
        for (IStepDecorator iStepDecorator : cntr.<IStepDecorator>getSonOf(IStepDecorator.class)) {
            int numOfNodes = iStepDecorator.getConfig().getNumOfNodes();
            if (numOfNodes > 0) {
                for (int i = 1; i <= numOfNodes - 1; i++) {
                    StepDecorator stepDecorator = new StepDecorator(iStepDecorator.getStep());
                    String stepDecoratorId = iStepDecorator.getId() + "_" + i;
                    stepDecorator.setId(stepDecoratorId);
                    stepDecorator.setDistributionNodeID(stepDecorator.getStep().getClass().getName());
                    cntr.add(stepDecorator, stepDecoratorId);
                }
            }
        }
    }

    private void registerShutdownHook() {
        Thread shutDownThread = new Thread(this::close);
        shutDownThread.setName("ShutdownHookThread");
        Runtime.getRuntime().addShutdownHook(shutDownThread);
    }

    private void wakenRunners() {
        for (IRunning running : cntr.<IRunning>getSonOf(IRunning.class)) {
            running.awake();
        }
        if (runningAlgoTickCallback != null)
            runningAlgoTickCallbackFuture = runningAlgoTickCallback.awake();
    }

    private ContainerRegistrar builtinContainerRegistration() {
        ContainerRegistrar containerRegistrar = new ContainerRegistrar();
        containerRegistrar.add(BuiltinTypes.STEPPING_SHOUTER.name(), new Shouter(cntr, this));

        containerRegistrar.add(BuiltinSubjectType.STEPPING_DATA_ARRIVED.name(), new Subject(BuiltinSubjectType.STEPPING_DATA_ARRIVED.name()));
        containerRegistrar.add(BuiltinSubjectType.STEPPING_PUBLISH_DATA.name(), new Subject(BuiltinSubjectType.STEPPING_PUBLISH_DATA.name()));

        if (getConfig().getPerfSamplerStepConfig().isEnable()) {
            int interval = getConfig().getPerfSamplerStepConfig().getReportInterval();
            String packages = getConfig().getPerfSamplerStepConfig().getPackages();
            containerRegistrar.add(BuiltinTypes.PERFSAMPLER.name(), new PerfSamplerStep(interval,packages));
        }

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
                String idcallbnack = iStepDecorator.getStep().getClass().getName() + "_tickCallBack";
                RunningScheduled runningScheduled =  new RunningScheduled(idcallbnack,delay, initialDelay, timeUnit,
                        () -> {
                            try {
                                iStepDecorator.queueSubjectUpdate(new Data(cb), BuiltinSubjectType.STEPPING_TIMEOUT_CALLBACK.name());
                                cb.await();
                            } catch (Exception e) {
                                handle(e);
                            }
                        });
                cntr.add(runningScheduled);
                runnersController.addScheduledRunner(runningScheduled.getScheduledExecutorService());
            }
            cntr.add(new Running(() -> {
                try {
                    iStepDecorator.openDataSink();
                } catch (Exception e) {
                    handle(e);
                }
            }, runnersController.getExecutorService()));
        }

        if (this.getConfig().isEnableTickCallback()) {
            this.runningAlgoTickCallback = new RunningScheduled(this.getClass().getName(),
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
            runnersController.addScheduledRunner(((RunningScheduled) this.runningAlgoTickCallback).getScheduledExecutorService());
        }
    }

    private void initSteps() {
        for (IStepDecorator step : cntr.<IStepDecorator>getSonOf(IStepDecorator.class)) {
            step.init(cntr);
            step.setAlgoConfig(getConfig());
        }
    }

    private void attachSubjects() {
        List<IStepDecorator> iStepDecoratorList =  cntr.<IStepDecorator>getSonOf(IStepDecorator.class);
        for (IStepDecorator iStepDecorator : iStepDecoratorList) {
            iStepDecorator.attachSubjects();
        }
    }

    private <T> void DI(T obj, String id) {
        if (obj instanceof Step && ((Step) obj).getId() != null) {
            cntr.add(obj, ((Step) obj).getId());
        } else {
            cntr.add(obj, id);
        }
    }

    private <T> void DI(T obj) {
        if (obj instanceof Step && ((Step) obj).getId() != null) {
            if ((((Step) obj).getId() == null)) {
                cntr.add(obj, ((Step) obj).getId());
            } else {
                cntr.add(obj);
            }
        }

    }

    private void DI(HashMap<String, Object> objs) {
        objs.forEach((s, o) -> DI(o, s));
    }


    @Override
    public boolean handle(Exception e) {
        synchronized (closingLock) {
            if (isClosed || delegateExceptionHandling(e))
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
            if (isClosed && delegateExceptionHandling(e))
                return;
            String error = "Exception Detected in Step - " + e.getStepId();
            logger.error(error, e);
            close();
        }
    }

    @Override
    public void handle(SteppingSystemException e) {
        synchronized (closingLock) {
            if (isClosed && delegateExceptionHandling(e))
                return;
            String error = "Exception Detected";
            if (e instanceof SteppingDistributionException)
                error += " while distributing Subject - " + ((SteppingDistributionException) e).getSubjectType();
            logger.error(error, e);
            close();
        }
    }

    @Override
    public void close() {
        synchronized (closingLock) {
            try {
                if (isClosed)
                    return;
                List<Closeable> closeables = cntr.getSonOf(IStepDecorator.class);
                for (Closeable closable : closeables) {
                    try {
                        closable.close();
                    } catch (Exception e) {
                        logger.error("Failed to close a closeable object, continuing with the next one");
                    }
                }

                //todo use shouter?
                List<IStepDecorator> stepDecorators = cntr.getSonOf(IStepDecorator.class);
                for (IStepDecorator step : stepDecorators) {
                    step.queueSubjectUpdate(new Data("cyanide"), "POISON-PILL");
                }
            } finally {
                isClosed = true;
                runnersController.kill();
                if (runningAlgoTickCallbackFuture != null)
                    runningAlgoTickCallbackFuture.cancel(true);

                if (steppingConfig.isKillProcessOnException())
                    killProcess();
            }
        }
    }

    private void killProcess() {
        try {
            //* todo: I know it is ugly... need to implement a better way to control Stepping from outside
            String vmName = ManagementFactory.getRuntimeMXBean().getName();
            int p = vmName.indexOf("@");
            String pid = vmName.substring(0, p);
            Runtime.getRuntime().exec("kill " + pid);
        } catch (Exception e) {
            logger.error("killProcess failed" + e.toString());
        }
    }

    private boolean delegateExceptionHandling(Exception e) {
        logger.info("Try delegate Exception to custom Exception Handler");
        List<IExceptionHandler> ehs = cntr.getSonOf(IExceptionHandler.class);
        if (ehs == null || ehs.isEmpty()) {
            logger.info("Custom Exception Handler MISSING");
            return false;
        }
        IExceptionHandler eh = ehs.get(0);
        try {
            boolean handle = eh.handle(e);
            if (!handle)
                logger.debug("Custom Exception Handler was not able to fully handle the Exception");
            return handle;
        } catch (Exception ex) {
            logger.error("Custom Exception Handler FAILED", ex);
            return false;
        }
    }

    @Override
    public void setSteppingConfig(SteppingConfig steppingConfig) {
        this.steppingConfig = steppingConfig;
    }
}