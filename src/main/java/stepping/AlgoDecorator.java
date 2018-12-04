package stepping;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class AlgoDecorator implements IExceptionHandler, IAlgoDecorator {
    static final Logger logger = LoggerFactory.getLogger(AlgoDecorator.class);

    private volatile Container cntr = new Container();
    private Algo algo;
    private IRunning running;
    private volatile CyclicBarrier cb;
    private boolean isClosed = false;

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
            close();
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
        containerRegistrar.add(BuiltinTypes.STEPPING_EXCEPTION_HANDLER.name(), this);
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

    @Override
    public void close() {
        try {
            logger.info("Try close entire Algo players");
            if (isClosed) {
                logger.debug("Algo already closed");
                return;

            }
//            if (this.cb != null)
//                cb.reset();
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
            this.running.close();
        } catch (Exception e) {
            logger.error("Failed to close Algo");
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
                long delay = iStepDecorator.getStep().getConfig() != null ? iStepDecorator.getStep().getConfig().getRunningPeriodicDelay() : globConf.getRunningPeriodicDelay();
                long initialDelay = iStepDecorator.getStep().getConfig() != null ? iStepDecorator.getStep().getConfig().getRunningInitialDelay() : globConf.getRunningInitialDelay();
                CyclicBarrier cb = new CyclicBarrier(2);
                this.cb = cb;
                TimeUnit timeUnit = iStepDecorator.getConfig().getRunningPeriodicDelayUnit();
                cntr.add(new RunningScheduled(delay, initialDelay, timeUnit,
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
            this.running = new RunningScheduled(
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
    public boolean handle(Exception e) {

        String error = "Exception Detected";
        if (e instanceof SteppingException)
            error += " in Step - " + ((SteppingException) e).getStepId();
        if (e instanceof DistributionException)
            error += " while distributing Subject - " + ((DistributionException) e).getSubjectType();
        logger.error(error, e);

        List<IExceptionHandler> exceptionHandlers = cntr.getSonOf(IExceptionHandler.class);
        if (exceptionHandlers != null && !exceptionHandlers.isEmpty()) {
            logger.debug("Forwarding exception to custom ExceptionHandlers");
            for (IExceptionHandler handler : exceptionHandlers) {
                boolean isExceptionHandled = handler.handle(e);
                if (isExceptionHandled)
                    return true;
            }
        }
        logger.debug("Forwarding responsibility to Main ExceptionHandler");
        close();
        return true;
    }
}