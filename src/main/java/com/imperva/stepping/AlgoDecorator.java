package com.imperva.stepping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class AlgoDecorator implements IExceptionHandler, IAlgoDecorator {
    private final Logger logger = LoggerFactory.getLogger(AlgoDecorator.class);
    private Container cntr = new ContainerDefaultImpl();
    private Container cntrPublic = new ContainerService();
    private Algo algo;
    private RunnersController runnersController = new RunnersController();//* todo Use CompletionService
    private volatile boolean isClosed = false;
    private final Lock closingLock = new ReentrantLock();
    private final int closingLockWaitDuration = 1;//* in seconds
    private final int poisonPillWaitDuration = 3000;

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

            logger.info("Fill Auto Created Subjects in Container...");
            fillAutoCreatedSubjectsInContainer();

            logger.info("Duplicating Parallel Nodes Steps...");
            duplicateNodes();

            logger.info("Populating public container...");
            fillPublicContainer();

            logger.info("Initializing Steps");
            initSteps();

            logger.info("Initializing Runners...");
            initRunners();

            logger.info("Register ShutdownHook...");
            registerShutdownHook();

            logger.info("Attach Subjects to Followers...");
            attachSubjects();

            logger.info("Starting Restate stage...");
            restate();

            logger.debug("Q dependency injection");
            QDependencyInjection();

            logger.debug("Run Steps...");
            wakenRunners();

            logger.debug("Init Algo...");
            algo.init();
        } catch (Exception e) {
            logger.error("Algo initialization FAILED", e);
            handle(e);
        } catch (Error err) {
            logger.error("Algo initialization FAILED - ERROR", err);
            handle(err);
        }
    }

    private void QDependencyInjection() {
        for (IStepDecorator iStepDecorator : cntr.<IStepDecorator>getSonOf(IStepDecorator.class)) {
            Q q = new Q<>(iStepDecorator.getConfig().getBoundQueueCapacity());
            String distributionID = iStepDecorator.getDistributionNodeID();
            for (IStepDecorator iStepDecorator2 : cntr.<IStepDecorator>getSonOf(IStepDecorator.class)) {
                if (iStepDecorator2.getQ() != null)
                    continue;
                if (iStepDecorator2.getConfig().getNumOfNodes() == 0) {
                    iStepDecorator2.setQ(new Q<>(iStepDecorator2.getConfig().getBoundQueueCapacity()));
                    logger.debug("Injecting regular Q with " + iStepDecorator2.getConfig().getBoundQueueCapacity() + " 'BoundQueueCapacity' to StepDecorator " + iStepDecorator2.getId());
                    continue;
                }
                if (iStepDecorator2.getDistributionNodeID().equals(distributionID) && iStepDecorator2.getConfig().getDistributionStrategy() instanceof SharedDistributionStrategy) {
                    iStepDecorator2.setQ(q);
                    logger.debug("Injecting SharedDistributionStrategy Q with " + iStepDecorator2.getConfig().getBoundQueueCapacity() + " 'BoundQueueCapacity' to StepDecorator " + iStepDecorator2.getId());
                }
            }
        }
    }

    private void fillAutoCreatedSubjectsInContainer() {
        ContainerRegistrar autoSubjectsRegistration = autoSubjectsRegistration();
        cntr.add(autoSubjectsRegistration.getRegistered());
    }

    private void fillContainer() {
        ContainerRegistrar builtinRegistration = builtinContainerRegistration();
        ContainerRegistrar objectsRegistration = containerRegistration();

        cntr.add(builtinRegistration.getRegistered());
        cntr.add(objectsRegistration.getRegistered());
    }

    private void fillPublicContainer() {
        List<Identifiable> identifiables = cntr.getAll();
        for (Identifiable iden : identifiables) {
            Object obj = iden.get();
            if (obj instanceof Step ||
                    obj instanceof Algo ||
                    obj instanceof IRunning) {//* TODO obj instanceof Subject. Can't remove them as we need them for the follower flow. Can be fixed with __STEPPING_PRIVATE_CONTAINER__
                continue;
            }
            cntrPublic.add(iden);
        }
        cntrPublic.add(cntr,"__STEPPING_PRIVATE_CONTAINER__");
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
            String decoratorId = step.getId() + ".decorator";
            stepDecorator.setId(decoratorId);
            cntr.add(stepDecorator, decoratorId);
        }
    }

    private void duplicateNodes() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        for (IStepDecorator iStepDecoratorToDuplicate : cntr.<IStepDecorator>getSonOf(IStepDecorator.class)) {
            int numOfNodes = iStepDecoratorToDuplicate.getConfig().getNumOfNodes();
            if (numOfNodes > 0) {
                for (int i = 1; i <= numOfNodes - 1; i++) {
                    Step currentStep = iStepDecoratorToDuplicate.getStep();
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
                    String stepDecoratorId = iStepDecoratorToDuplicate.getStep().getId() + "." + i + ".decorator";
                    stepDecorator.setId(stepDecoratorId);

                    String distId = stepDecorator.getStep().getClass().getName();
                    stepDecorator.setDistributionNodeID(distId);
                    iStepDecoratorToDuplicate.setDistributionNodeID(distId);

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
    }

    private ContainerRegistrar builtinContainerRegistration() {
        ContainerRegistrar containerRegistrar = new ContainerRegistrar();
        containerRegistrar.add(BuiltinTypes.STEPPING_SHOUTER.name(), new Shouter(cntr, this));

        containerRegistrar.add(BuiltinSubjectType.STEPPING_DATA_ARRIVED.name(), new Subject(BuiltinSubjectType.STEPPING_DATA_ARRIVED.name()));
        containerRegistrar.add(BuiltinSubjectType.STEPPING_PUBLISH_DATA.name(), new Subject(BuiltinSubjectType.STEPPING_PUBLISH_DATA.name()));

        if (getConfig().getPerfSamplerStepConfig().isEnable()) {
            int interval = getConfig().getPerfSamplerStepConfig().getReportInterval();
            String packages = getConfig().getPerfSamplerStepConfig().getPackages();
            if (packages == null || packages.trim().equals(""))
                throw new SteppingException("'packages' list field is required to initialize PerfSamplerStep");
            containerRegistrar.add(new PerfSamplerStep(interval, packages));
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
    public Container getContainer() {
        return cntrPublic;
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
                            } catch (Error err) {
                                handle(err);
                            }

                        });
                cntr.add(runningScheduled, runnerScheduledID);
                runnersController.addScheduledRunner(runningScheduled.getScheduledExecutorService());
            }
            cntr.add(new Running(() -> {
                while (true) {
                    try {
                        iStepDecorator.openDataSink();
                    } catch (Exception e) {
                        if (!handle(e)) {
                            logger.debug("Exception was NOT handled successfully, re-opening DataSink");
                            break;
                        } else {
                            logger.debug("Exception was handled, re-opening DataSink ");
                        }

                    } catch (Error err) {
                        if (!handle(err)) {
                            logger.debug("Error was NOT handled successfully, re-opening DataSink");
                            break;
                        } else {
                            logger.debug("Error was handled, re-opening DataSink ");
                        }
                    }
                }
            }, runnersController.getExecutorService()));
        }

        if (this.getConfig().isEnableTickCallback()) {
            RunningScheduled runningScheduledAlgo = new RunningScheduled(this.getClass().getName(),
                    globConf.getRunningPeriodicDelay(),
                    globConf.getRunningInitialDelay(),
                    TimeUnit.MILLISECONDS,
                    () -> {
                        try {
                            onTickCallBack();
                        } catch (Exception e) {
                            handle(e);
                        } catch (Error err) {
                            handle(err);
                        }
                    });
            cntr.add(runningScheduledAlgo, this.getClass().getName());
            runnersController.addScheduledRunner(runningScheduledAlgo.getScheduledExecutorService());
        }
    }

    private void initSteps() {
        for (IStepDecorator step : cntr.<IStepDecorator>getSonOf(IStepDecorator.class)) {
            step.init(cntrPublic);
            step.setAlgoConfig(getConfig());
        }
    }

    private void attachSubjects() {
        List<IStepDecorator> iStepDecoratorList = cntr.<IStepDecorator>getSonOf(IStepDecorator.class);
        for (IStepDecorator iStepDecorator : iStepDecoratorList) {
            iStepDecorator.attachSubjects();
        }
    }

    @Override
    public boolean handle(Exception e) {
        try {
            if (!closingLock.tryLock(closingLockWaitDuration, TimeUnit.SECONDS))
                return true;

            if (isClosed)
                return false;

            if(e instanceof SteppingExceptionError){
                SteppingExceptionError err = (SteppingExceptionError)e;
                if(delegateExceptionHandling((Error)err.getCause()))
                    return true;
            } else if (delegateExceptionHandling(e))
                return true;


            String error = "Exception Detected";
            if (e instanceof IdentifiableSteppingException)
                error += " in Step Id: " + ((IdentifiableSteppingException) e).getStepId();
            else if (e instanceof SteppingDistributionException)
                error += " while distributing Subject - " + ((SteppingDistributionException) e).getSubjectType();
            logger.error(error, e);

            closeAndKillIfNeeded(e);
        } catch (InterruptedException e1) {
            logger.error("tryLock was interrupted", e);
            return false;
        } finally {
            closingLock.unlock();
        }
        return false;
    }

    @Override
    public boolean handle(Error err) {
        logger.error("Handling Error :" + err.toString() + ". Converting to SteppingExceptionError");
        return handle(new SteppingExceptionError(err));
    }

    @Override
    public void close() {
        try {

            if (isClosed)
                return;

            if (!closingLock.tryLock(closingLockWaitDuration, TimeUnit.SECONDS))
                return;

            if (isClosed)
                return;

            closeStepDecorators();

            sendPoisonPill();
            Thread.sleep(poisonPillWaitDuration);

            closeRunners();

            closeAlgo();

            isClosed = true;
            /* this is located here and not inside finally because we want to make sure all the prev steps were taken */
        } catch (InterruptedException e) {
            logger.error("tryLock interrupted", e);
        } finally {
            closingLock.unlock();
        }
    }

    private void closeAndKillIfNeeded(Exception e) {
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
        if (c.isInstance(e))
            return true;

        Throwable cause = null;
        Throwable result = e;

        while (null != (cause = result.getCause())) {
            result = cause;
            if (c.isInstance(cause))
                return true;
        }
        return false;
    }

    private void closeRunners() {
        logger.debug("Closing Runners");

        for (IRunning running : cntr.<IRunning>getSonOf(IRunning.class)) {
            try {
                running.close();
            } catch (Exception e) {
                logger.error("Failed to close a Runner in Algo " + this.algo.getClass(), e);
            }
        }

        try {
            runnersController.kill();
        } catch (Exception e) {
            logger.error("Failed to Kill Runners in Algo " + this.algo.getClass(), e);
        }
    }

    private void closeAlgo() {
        logger.debug("Closing Algo");
        try {
            this.algo.close();
        } catch (IOException e) {
            logger.error("Failed to close Algo " + this.algo.getClass(), e);
        }
    }

    private void closeStepDecorators() {
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
        } catch (Exception e) {
            logger.error("Failed to close Closeables in Algo " + this.algo.getClass(), e);
        }
    }

    private void sendPoisonPill() {
        logger.debug("Sending Poison Pill");
        try {
            List<IStepDecorator> stepDecorators = cntr.getSonOf(IStepDecorator.class);
            for (IStepDecorator step : stepDecorators) {
                step.queueSubjectUpdate(new Data("cyanide"), "POISON-PILL");
            }
        } catch (Exception e) {
            logger.error("Failed to send poison pill in Algo " + this.algo.getClass(), e);
        }
    }

    private void killProcess() {
        logger.warn("Gracefully killing process. It should take " + closingLockWaitDuration + " seconds");
        System.exit(1);
    }

    private boolean delegateExceptionHandling(Throwable e) {
        logger.info("Try delegate Exception/Error to custom Exception Handler", e);
        IExceptionHandler customExceptionHandler = getConfig().getCustomExceptionHandler();
        if (customExceptionHandler == null) {
            logger.info("Custom Exception/Error Handler MISSING");
            return false;
        }
        try {

            boolean handle = false;
            if (e instanceof Exception) {
                handle = customExceptionHandler.handle((Exception) e);
            } else if (e instanceof Error) {
                handle = customExceptionHandler.handle((Error) e);
            }

            if (!handle)
                logger.debug("Custom Exception/Error Handler was not able to fully handle the Exception");
            else
                logger.debug("Custom Exception/Error Handler fully handled the Exception");

            return handle;
        } catch (SteppingSystemCriticalException ex) {
            logger.error("Custom Exception/Error Handler throw SteppingSystemCriticalException", ex);
            closeAndKillIfNeeded(ex);
            return false;
        } catch (Exception ex) {
            logger.error("Custom Exception/Error Handler FAILED", ex);
            return false;
        }
    }
}