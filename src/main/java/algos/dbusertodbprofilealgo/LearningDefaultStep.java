package algos.dbusertodbprofilealgo;

import Stepping.*;
public class LearningDefaultStep implements Step {

    public LearningDefaultStep() {
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
    public void newDataArrivedCallBack(Data data, SubjectContainer subjectContainer) {
        if (DefaultSubjectType.STEPPING_DATA_ARRIVED.name().equals(data.getSubjectType())) {
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
