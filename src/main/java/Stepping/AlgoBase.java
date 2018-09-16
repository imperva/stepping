package Stepping;

import Stepping.defaultsteps.DefaultSubjectType;
import Stepping.defaultsteps.ExternalDataConsumerStep;
import Stepping.defaultsteps.ExternalDataProducerStep;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AlgoBase extends IAlgo implements IExternalDataReceiver {

    protected Q q = new Q<Data>();
    private Container cntr = new Container();
    private IMessenger iMessenger;
    protected AlgoBase(String id){
        super(id);
    }

    @Override
    public void run() {
        List<Data> subjectList = q.take();
        if (subjectList.size() > 0) {
            for (Data data : subjectList) {
                newDataArrivedCallBack(data);
            }
        } else {
            tickCallBack();
        }
    }

    @Override
    public AlgoInfraConfig init() {


        DI(new SubjectContainer(), DefaultID.SUBJECT_CONTAINER.name());

        IoC();
        initSteps();
        initSubjects();
        regiterShutdownHook();
        attachSubjects();
        attachExternalDataReceiver();
        restate();

        //wakenProcessingUnit();
        wakenAllProcessingUnit();
        return null;
    }

    protected abstract void attachExternalDataReceiver();

    private void regiterShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    private void wakenAllProcessingUnit() {
        for (IRunning running : cntr.<IStep>getSonOf(IRunning.class)) {
          running.wakenProcessingUnit();
        }
    }

    //todo Add abstract method IoC that each child will need to implement
    //todo Add iMessenger to ExternalDataConsumerStep and remove it from all steps
    @Override
    protected void IoC() {
        DI(new Subject(DefaultSubjectType.S_DATA_ARRIVED.name()), DefaultSubjectType.S_DATA_ARRIVED.name());
        if (iMessenger != null) {
            DI(new ExternalDataConsumerStep(), DefaultID.EXTERNAL_DATA_CONSUMER.name());
            DI(new ExternalDataProducerStep(), DefaultID.EXTERNAL_DATA_PRODUCER.name());
        }
    }

    @Override
    protected void setMessenger(IMessenger messenger){
        this.iMessenger = messenger;
    }

    @Override
    public void close(){
        if (iMessenger != null) {
            iMessenger.shutdown();
        }

        for (Closeable closable: getContainer().<Closeable>getSonOf(Closeable.class)) {
            try {
                closable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private void initSubjects(){
        for (Subject subject: getContainer().<Subject>getTypeOf(Subject.class)) {
            SubjectContainer subjectContainer = getSubjectContainer();
            subjectContainer.add(subject);
            subject.setContainer(cntr);
        }
    }

    private void restate(){
        List<Thread> threads = new ArrayList<>();
        for (IStep step : cntr.<IStep>getSonOf(IStep.class)) {
            Thread thread = new Thread(()->{ step.restate();});
            thread.setName("restate: " + step.getClass().getName());
            thread.start();
            threads.add(thread);
        }

        threads.forEach((t)->{
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void initSteps(){
        for (IStep step : cntr.<IStep>getSonOf(IStep.class)) {
            step.init();
            step.setContainer(cntr);
            step.setMessenger(iMessenger);
        }
    }

    private void attachSubjects(){
        SubjectContainer subjectContainer = getContainer().getById(DefaultID.SUBJECT_CONTAINER.name());

        for (IStep step : cntr.<IStep>getSonOf(StepBase.class)) {
            for (ISubject subject : subjectContainer.getSubjectsList()) {
                step.attach(subject);
            }
        }
    }

    protected SubjectContainer getSubjectContainer(){
        return getContainer().getById(DefaultID.SUBJECT_CONTAINER.name());
    }

    protected Container getContainer(){
        return cntr;
    }

    protected  <T> void DI(T obj, String id){
        cntr.add(obj, id);
    }
}
