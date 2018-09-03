package Stepping;

import Stepping.container.Container;
import alogs.etlalgo.SubjectType;

import java.util.List;


public abstract class AlgoBase extends IAlgo {

    private Q q = new Q();
    private Container cntr = new Container();
    private IMessenger iMessenger;

    protected AlgoBase(String id){ super(id);

    }

    @Override
    public void run() {
        while (true) {
//            try {
//                Thread.sleep(2);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            List x = q.take();
            if(x.size() > 0) {
                System.out.println("@@@@ DATA");
                for (Object o : x) {
                    start(new Data<>(o));
                }
            }
            //else
                //System.out.println("NO DATA@@@@");
        }
    }

    @Override
    public AlgoInfraConfig init() {
        DI(new Subject("newDataArrivedSubject"), "newDataArrivedSubject");
        DI(new SubjectContainer(), "subjectContainer");


        IoC();
        initSteps();

        for (Subject subject: getContainer().<Subject>getTypeOf(Subject.class)             ) {
            SubjectContainer subjectContainer = getContainer().getById("subjectContainer");
            subjectContainer.add(subject);
            subject.setContainer(cntr);
        }

        attachSubjects();

        wakenProcessingUnit();
        return null;
    }

    @Override
    public void newDataArrived(Data<?> data) {
        data.getValue();
        q.queue(data);
    }

    @Override
    public void setMessenger(IMessenger messenger){
        this.iMessenger = messenger;
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


    abstract protected void IoC();
    abstract protected void start(Data data);

}
