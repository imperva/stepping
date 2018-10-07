package Stepping;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

public class DefaultAlgoDecorator implements IExceptionHandler, IAlgoDecorator {
    private Container cntr = new Container();
    private IMessenger iMessenger;
    private Algo algo;
    private Running running;
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
            close();
        }
    }

    private void registerIoC() {
        HashMap<String, Object> defaultIoCMap = DefaultIoC();
        HashMap<String, Object> IoCMap = IoC();
        defaultIoCMap.putAll(IoCMap);

        DI(defaultIoCMap);
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
        for (Running running : cntr.<Running>getSonOf(Running.class)) {
            running.awake();
        }
        running.awake();
    }

    //todo create a dedicated IoC object. don't work directly with lists
    private HashMap<String, Object> DefaultIoC() {
        HashMap<String, Object> objectHashMap = new HashMap<>();
        objectHashMap.put(DefaultIoCID.STEPPING_SUBJECT_CONTAINER.name(), new SubjectContainer());

        objectHashMap.put(DefaultSubjectType.STEPPING_DATA_ARRIVED.name(), new Subject(DefaultSubjectType.STEPPING_DATA_ARRIVED.name()));
        objectHashMap.put(DefaultSubjectType.STEPPING_PUBLISH_DATA.name(), new Subject(DefaultSubjectType.STEPPING_PUBLISH_DATA.name()));
        if (iMessenger != null) {
            ExternalDataConsumerDefaultStep externalDataConsumerStep = new ExternalDataConsumerDefaultStep();
            externalDataConsumerStep.setMessenger(iMessenger);
            ExternalDataProducerDefaultStep externalDataProducerStep = new ExternalDataProducerDefaultStep();
            externalDataProducerStep.setMessenger(iMessenger);
            objectHashMap.put(DefaultIoCID.STEPPING_EXTERNAL_DATA_CONSUMER.name(), externalDataConsumerStep);
            objectHashMap.put(DefaultIoCID.STEPPING_EXTERNAL_DATA_PRODUCER.name(), externalDataProducerStep);
        }
        return objectHashMap;
    }

    @Override
    public void tickCallBack() {
//        List<Integer> xx = new ArrayList<>();
//        for (int u=0; u< 298; u++) {
//            xx.add(u);
//        }
//        getSubjectContainer().getByName(DefaultSubjectType.STEPPING_DATA_ARRIVED.name()).setData(new Data(xx));
//       for (int u=0; u< 100000000; u++) {
//           try {
//               Thread.sleep(2000);
//           } catch (InterruptedException e) {
//               e.printStackTrace();
//           }
//           getSubjectContainer().getByName(DefaultSubjectType.STEPPING_DATA_ARRIVED.name()).setData(new Data(new ArrayList<>()));
//       }
// //       if(u > 1000000 && u < 1000080) {
        //            try {
//
//                    Thread.sleep(50000000);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
////            }else if(u > 10007 && u < 10100){
////                try {
////                    Thread.sleep(1000);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
////            }else if(u > 10100){
////                try {
////                    Thread.sleep(100);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
////            }
//            }
//
//
//
//        }

        algo.tickCallBack();
    }

    @Override
    public GlobalAlgoStepConfig getGlobalAlgoStepConfig() {
        return algo.getGlobalAlgoStepConfig();
    }

    @Override
    public void setMessenger(IMessenger messenger) {
        this.iMessenger = messenger;
    }

    @Override
    public HashMap<String, Object> IoC() {
        return algo.IoC();
    }

    @Override
    public void close() {
        try {
            if (isClosed)
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
            if (iMessenger != null)
                this.iMessenger.close();
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
            boolean isDaemon = iStepDecorator.getStep().getLocalStepConfig() != null ? iStepDecorator.getStep().getLocalStepConfig().isRunningAsDaemon() : globConf.isRunningAsDaemon();

            cntr.add(new Running(iStepDecorator.getStep().getClass().getName(),
                    iStepDecorator,
                    delay,
                    initialDelay,
                    isDaemon));
        }

        this.running = new Running(DefaultAlgoDecorator.class.getName(), this,
                globConf.getRunningPeriodicDelay(),
                globConf.getRunningInitialDelay(),
                globConf.isRunningAsDaemon());
    }

    private void initSteps() {
        for (IStepDecorator step : cntr.<IStepDecorator>getSonOf(IStepDecorator.class)) {
            step.init();
            step.setContainer(cntr);
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
    public void run() {
        tickCallBack();
    }

    @Override
    public void handle(Exception e) {
        System.out.println("Error: " + e.toString());
        close();
    }
}