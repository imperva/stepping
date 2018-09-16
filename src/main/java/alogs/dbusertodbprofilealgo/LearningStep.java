package alogs.dbusertodbprofilealgo;

import Stepping.ISubject;
import Stepping.StepBase;
import Stepping.SubjectContainer;
import Stepping.defaultsteps.DefaultSubjectType;

public class LearningStep extends StepBase {

    public LearningStep() {
        super(LearningStep.class.getName());
    }

    @Override
    public void attach(ISubject iSubject) {
        if (DefaultSubjectType.S_DATA_ARRIVED.name().equals(iSubject.getType())) {
            iSubject.attach(this);
        }
    }

    @Override
    public void shutdown() {

    }

    @Override
    protected void newDataArrivedCallBack(ISubject subject, SubjectContainer subjectContainer) {
        if (DefaultSubjectType.S_DATA_ARRIVED.name().equals(subject.getType())) {
            System.out.println("LearningStep: newDataArrivedSubject Arrived!");
        }
    }

    @Override
    protected void tickCallBack() {

    }

    @Override
    public void restate() {

    }
}
