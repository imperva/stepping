package Stepping;

import java.util.List;


public abstract class AlgoBase extends IAlgo {

    private Q q = new Q<Data>();
    private Container cntr = new Container();
    private IMessenger iMessenger;

    protected AlgoBase(String id){ super(id);

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

        DI(new SubjectContainer(), "subjectContainer");


        IoC();
        initSteps();
        initSubjects();
        attachSubjects();

        wakenProcessingUnit();
        return null;
    }

    @Override
    public void newDataArrived(Data<?> data) {
        q.queue(data);
    }

    @Override
    public void setMessenger(IMessenger messenger){
        this.iMessenger = messenger;
    }

    private void initSubjects(){
        for (Subject subject: getContainer().<Subject>getTypeOf(Subject.class)             ) {
            SubjectContainer subjectContainer = getContainer().getById("subjectContainer");
            subjectContainer.add(subject);
            subject.setContainer(cntr);
        }
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
