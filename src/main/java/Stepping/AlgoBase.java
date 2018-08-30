package Stepping;

import Stepping.container.Container;

public abstract class AlgoBase extends IAlgo {
    private SubjectContainer subjectContainer = new SubjectContainer();
    private Container cntr;

    protected AlgoBase(String id, int delay, int initialdelay){ super(id, delay, initialdelay);
        cntr = Container.getInstance();
        cntr.add(subjectContainer, "subjectContainer");
    }

    @Override
    public void run() {
        while (true) {
            start(new Data<>());
        }
    }

    public AlgoInfraConfig init() {
        DI(new Subject(), "newDataArrivedSubject");
        DI(new SubjectContainer(), "subjectContainer");

        IoC();
        initSteps();
        attachSubjects();

        go();
        return null;
    }

    private void initSteps(){
        for (IStep step : Container.getInstance().<IStep>getTypeOf(IStep.class)) {
            step.init();
        }
    }

    private void attachSubjects(){
        SubjectContainer subjectContainer = getContainer().getById("subjectContainer");
        for (IStep step : Container.getInstance().<IStep>getTypeOf(IStep.class)) {
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

    public void newDataArrived(Data data) {
        //* in Q

    }
    abstract protected void IoC();
    abstract protected void start(Data data);

}
