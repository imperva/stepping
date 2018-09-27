package alogs.dbusertodbprofilealgo;

import Stepping.*;
import Stepping.defaultsteps.DefaultSubjectType;

import java.io.IOException;

public class LearningDefaultStep implements Step {

    public LearningDefaultStep() {

        //super(LearningDefaultStep.class.getName());
    }




    @Override
    public void init() {

    }

    @Override
    public boolean isAttach(String eventType) {
        if (DefaultSubjectType.S_DATA_ARRIVED.name().equals(eventType)) {
           return true;
        }
        return false;
    }

    @Override
    public void newDataArrivedCallBack(ISubject subject, SubjectContainer subjectContainer) {
        if (DefaultSubjectType.S_DATA_ARRIVED.name().equals(subject.getType())) {
            System.out.println("LearningDefaultStep: newDataArrivedSubject Arrived!");
        }
    }

    @Override
    public void tickCallBack() {

    }

    @Override
    public void restate() {

    }

    @Override
    public void setContainer(Container cntr) {

    }

    @Override
    public void close() throws IOException {

    }
}
