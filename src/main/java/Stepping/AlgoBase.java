package Stepping;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AlgoBase extends IAlgo implements IExternalDataReceiver {

    private Q q = new Q<Data>();
    private Container cntr = new Container();
    private IMessenger iMessenger;
    protected AlgoBase(String id){ super(id); }

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


        DI(new SubjectContainer(), "subjectContainer");

        IoC();
        initSteps();
        initSubjects();
        regiterShutdownHook();
        attachSubjects();
        restate();



        //wakenProcessingUnit();
        wakenAllProcessingUnit();
        return null;
    }

    private void regiterShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    private void wakenAllProcessingUnit() {
        for (IRunning running : cntr.<IStep>getSonOf(IRunning.class)) {
          running.wakenProcessingUnit();
        }
    }

    @Override
    public void newDataArrived(Data<?> data) {
        q.queue(data);
    }

    @Override
    protected void setMessenger(IMessenger messenger){
        this.iMessenger = messenger;
    }

    @Override
    public void close(){

        for (Closeable closable: getContainer().<Closeable>getSonOf(Closeable.class)) {
            try {
                closable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private void initSubjects(){
        for (Subject subject: getContainer().<Subject>getTypeOf(Subject.class)             ) {
            SubjectContainer subjectContainer = getContainer().getById("subjectContainer");
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

        threads.stream().forEach((t)->{
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
        SubjectContainer subjectContainer = getContainer().getById("subjectContainer");

        for (IStep step : cntr.<IStep>getSonOf(StepBase.class)) {
            step.init();
            for (ISubject subject : subjectContainer.getSubjectsList()) {
                step.attach(subject);
            }
        }
    }

    protected SubjectContainer getSubjectContainer(){
        return cntr.getById("subjectContainer");
    }

    protected Container getContainer(){
        return cntr;
    }

    protected  <T> void DI(T obj, String id){
        cntr.add(obj, id);
    }
}
