package alogs.etlalgo;


import Stepping.*;

public class PreProcessStep extends StepBase {
    int dataArribedIndex = 0;

    protected PreProcessStep() {
        super(PreProcessStep.class.getName());

    }

    public void attach(ISubject iSubject) {
        if (iSubject.getType() == "newDataArrivedSubject") {
            iSubject.attach(this);
        }
    }

    @Override
    protected void tickCallBack() {
        System.out.println("PreProcessStep TICKS");
    }

    @Override
    protected void newDataArrivedCallBack(ISubject subject, SubjectContainer subjectContainer) {
        if (subject.getType() == "newDataArrivedSubject") {
            System.out.println("PreProcessStep: newDataArrivedSubject Arrived!");
            dataArribedIndex++;
            if (dataArribedIndex == 5) {
                dataArribedIndex = 0;
                subjectContainer.getByName(SubjectType.PRE_PROCESS.name()).setData(new Data());
            }
        }
    }
}