package alogs.etlalgo;


import Stepping.*;

public class PreProcessStep extends StepBase {

    protected PreProcessStep() {
        super(PreProcessStep.class.getName());

    }

    public void attach(ISubject iSubject) {
        if (iSubject.getType() == "newDataArrivedSubject") {
            iSubject.attach(this);
        }
    }

    int dataArribedIndex = 0;

    @Override
    protected void start(ISubject subject, SubjectContainer subjectContainer) {
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