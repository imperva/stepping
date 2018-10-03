package algos.dbusertodbprofilealgo;

import Stepping.*;
public class LearningDefaultStep implements Step {

    public LearningDefaultStep() {

        //super(LearningDefaultStep.class.getName());
    }




    @Override
    public void init() {

    }

    @Override
    public boolean followsSubject(String subjectType) {
        if (DefaultSubjectType.STEPPING_DATA_ARRIVED.name().equals(subjectType)) {
           return true;
        }
        return false;
    }

    @Override
    public void newDataArrivedCallBack(ISubject subject, SubjectContainer subjectContainer) {
        if (DefaultSubjectType.STEPPING_DATA_ARRIVED.name().equals(subject.getType())) {
            System.out.println("LearningDefaultStep: newDataArrivedSubject Arrived!");
        }
    }

    @Override
    public void tickCallBack() {
        System.out.println("LearningDefaultStep");
    }

    @Override
    public void restate() {

    }

    @Override
    public void shuttingDown() {

    }

    @Override
    public void setContainer(Container cntr) {

    }

}