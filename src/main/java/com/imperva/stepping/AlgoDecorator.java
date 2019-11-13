package com.imperva.stepping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class AlgoDecorator implements IBuiltinExceptionHandler, IAlgoDecorator {
    private final Logger logger = LoggerFactory.getLogger(AlgoDecorator.class);
    private Container cntr = new ContainerService();
    private Algo algo;
    private IRunning runningAlgoTickCallback;
    private RunnersController runnersController = new RunnersController();//* todo Use CompletionService
    private volatile boolean isClosed = false;
    private final Lock closingLock = new ReentrantLock();
    private Future runningAlgoTickCallbackFuture;

    private int closingLockWaitDuration = 1;//* in seconds
    private int poisonPillWaitDuration = 3000;

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
            if (step.getId() == null || "".equals(step.getId().trim())) {
                throw new SteppingException("Step Object must contain an ID name");
            }
            String decoratorId = step.getId() + "." + "decorator";
            stepDecorator.setId(decoratorId);
            cntr.add(stepDecorator, decoratorId);
        }
    }

    private void duplicateNodes() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        for (IStepDecorator iStepDecorator : cntr.<IStepDecorator>getSonOf(IStepDecorator.class)) {
            int numOfNodes = iStepDecorator.getConfig().getNumOfNodes();
            if (numOfNodes > 0) {
                for (int i = 1; i <= numOfNodes - 1; i++) {
                    Step currentStep = iStepDecorator.getStep();
                    String currentStepId = currentStep.getId();
                    Step duplicatedStp = (Step) Class.forName(currentStep.getClass().getName(), true, currentStep.getClass().getClassLoader()).newInstance();
                    String stepID = currentStepId + "." + i;
                    try {
                        duplicatedStp.setId(stepID);
                        if (!duplicatedStp.getId().equals(stepID)) {
                            throw new SteppingException("Can't set Step id. Tried to set id: " + stepID + " but found: " + duplicatedStp.getId());
                        }
                    } catch (SteppingException ex) {
                        logger.error(ex.getMessage());
                        logger.error("Make sure setId() and getId() are implemented in Step: " + currentStep.getClass());
                        throw ex;
                    }

                    StepDecorator stepDecorator = new StepDecorator(duplicatedStp);
                    String stepDecoratorId = iStepDecorator.getId() + "." + i;
                    stepDecorator.setId(stepDecoratorId);
                    stepDecorator.setDistributionNodeID(stepDecorator.getStep().getClass().getName());
                    cntr.add(stepDecorator, stepDecoratorId);
                    cntr.add(duplicatedStp, duplicatedStp.getId());
                }
            }
        }
    }

    private void registerShutdownHook() {
        Thread shutDownThread = new Thread(this::close);
        shutDownThread.setName("ShutdownHookThread." + getClass().getName());
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
            if(packages == null || packages.trim().equals(""))
                throw new SteppingException("'packages' list field is required to initialize PerfSamplerStep");
            containerRegistrar.add(BuiltinTypes.PERFSAMPLER.name(), new PerfSamplerStep(interval, packages));
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
                String runnerScheduledID = iStepDecorator.getStep().getId() + ".runningScheduled";
                RunningScheduled runningScheduled = new RunningScheduled(runnerScheduledID, delay, initialDelay, timeUnit,
                        () -> {
                            try {
                                iStepDecorator.queueSubjectUpdate(new Data(cb), BuiltinSubjectType.STEPPING_TIMEOUT_CALLBACK.name());
                                cb.await();
                            } catch (Exception e) {
                                handle(e);
                            }
                        });
                cntr.add(runningScheduled, runnerScheduledID);
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
        List<IStepDecorator> iStepDecoratorList = cntr.<IStepDecorator>getSonOf(IStepDecorator.class);
        for (IStepDecorator iStepDecorator : iStepDecoratorList) {
            iStepDecorator.attachSubjects();
        }
    }

    private <T> void DI(T obj, String id) {
        cntr.add(obj, id);
    }

    private void DI(HashMap<String, Object> objs) {
        objs.forEach((s, o) -> DI(o, s));
    }

    @Override
    public boolean handle(Exception e) {
        try {
            closingLock.tryLock(closingLockWaitDuration, TimeUnit.SECONDS);
            if (isClosed || delegateExceptionHandling(e))
                return true;
            String error = "Exception Detected";
            logger.error(error, e);
            closeAndTryKill(e);
        } catch (InterruptedException e1) {
            logger.error("tryLock was interrupted", e);
        } finally {
            closingLock.unlock();
        }
        return true;
    }

    @Override
    public void handle(SteppingException e) {
        try {
            closingLock.tryLock(closingLockWaitDuration, TimeUnit.SECONDS);
            if (isClosed && delegateExceptionHandling(e))
                return;
            String error = "Exception Detected in stepping";
            logger.error(error, e);
            closeAndTryKill(e);
        } catch (InterruptedException e1) {
            logger.error("tryLock was interrupted", e);
        } finally {
            closingLock.unlock();
        }
    }

    @Override
    public void handle(IdentifiableSteppingException e) {
        try {
            closingLock.tryLock(closingLockWaitDuration, TimeUnit.SECONDS);
            if (isClosed && delegateExceptionHandling(e))
                return;
            String error = "Exception Detected in Step - " + e.getStepId();
            logger.error(error, e);
            closeAndTryKill(e);
        } catch (InterruptedException e1) {
            logger.error("tryLock was interrupted", e);
        } finally {
            closingLock.unlock();
        }
    }

    @Override
    public void handle(SteppingSystemException e) {
        try {
            closingLock.tryLock(closingLockWaitDuration, TimeUnit.SECONDS);
            if (isClosed && delegateExceptionHandling(e))
                return;
            String error = "Exception Detected";
            if (e instanceof SteppingDistributionException)
                error += " while distributing Subject - " + ((SteppingDistributionException) e).getSubjectType();
            logger.error(error, e);
            closeAndTryKill(e);
        } catch (InterruptedException e1) {
            logger.error("tryLock was interrupted", e);
        } finally {
            closingLock.unlock();
        }
    }

    @Override
    public void close() {
        try {

            if (isClosed)
                return;

            closingLock.tryLock(closingLockWaitDuration, TimeUnit.SECONDS);

            if (isClosed)
                return;

            closeCloseables();

            sendPoisonPill();
            Thread.sleep(poisonPillWaitDuration);

            closeRunners();

            closeAlgo();

            isClosed = true;

        } catch (InterruptedException e) {
            logger.error("tryLock interrupted", e);
        } finally {
            closingLock.unlock();
        }
    }

    private void closeAndTryKill(Exception e) {
        try {
            logger.error("Try to close and kill", e);
            close();
        } finally {
            if (containsInChain(e, SteppingSystemCriticalException.class)) {
                killProcess();
            }
        }
    }

   private boolean containsInChain(Throwable e, Class c) {
        if(c.isInstance(e))
            return true;

        Throwable cause = null;
        Throwable result = e;

        while(null != (cause = result.getCause())  ) {
            result = cause;
            if(c.isInstance(cause))
                return true;
        }
        return false;
    }

    private void closeRunners() {
        logger.info("Closing Runners");
        try {
            runnersController.kill();
            if (runningAlgoTickCallbackFuture != null) {
                runningAlgoTickCallbackFuture.cancel(true);
            }
        } catch (Exception e) {
            logger.error("Failed to close Runners in Algo " + this.algo.getClass(), e);
        }
    }

    private void closeAlgo(){
        logger.debug("Closing Algo");
        try {
            this.algo.close();
        } catch (IOException e) {
            logger.error("Failed to close Algo " + this.algo.getClass(), e);
        }
    }

    private void closeCloseables(){
        logger.debug("Closing Closeables");
        try {
            List<Closeable> closeables = cntr.getSonOf(IStepDecorator.class);
            for (Closeable closable : closeables) {
                try {
                    closable.close();
                } catch (Exception e) {
                    logger.error("Failed to close a closeable object, continuing with the next one");
                }
            }
        }catch (Exception e){
            logger.error("Failed to close Closeables in Algo " + this.algo.getClass(), e);
        }
    }

    private void sendPoisonPill(){
        logger.debug("Sending Poison Pill");
        try {
            //todo use shouter?
            List<IStepDecorator> stepDecorators = cntr.getSonOf(IStepDecorator.class);
            for (IStepDecorator step : stepDecorators) {
                step.queueSubjectUpdate(new Data("cyanide"), "POISON-PILL");
            }
        }catch (Exception e){
            logger.error("Failed to send poison pill in Algo " + this.algo.getClass(), e);
        }
    }

    private void killProcess() {
        logger.warn("Gracefully killing process. It should take " + closingLockWaitDuration + " seconds");
        System.exit(1);
    }

    private boolean delegateExceptionHandling(Exception e) {
        logger.info("Try delegate Exception to custom Exception Handler", e);
        IExceptionHandler customExceptionHandler = getConfig().getCustomExceptionHandler();
        if (customExceptionHandler == null) {
            logger.info("Custom Exception Handler MISSING");
            return false;
        }
        try {
            boolean handle = customExceptionHandler.handle(e);
            if (!handle)
                logger.debug("Custom Exception Handler was not able to fully handle the Exception");
            return handle;
        } catch (SteppingSystemCriticalException ex) {
            logger.error("Custom Exception Handler throw SteppingSystemCriticalException", ex);
            closeAndTryKill(ex);
            return true;
        } catch (Exception ex) {
            logger.error("Custom Exception Handler FAILED", ex);
            return false;
        }
    }
}