package alogs.etlalgo;

import Stepping.*;

public class InOutCounterDefaultStep implements Step {
    private long counterProduce;
    private long counterConsume;

    InOutCounterDefaultStep() {
    }

    @Override
    public void init() {

    }

    @Override
    public boolean isAttach(String subjectType) {
        if(subjectType.equals(DefaultSubjectType.STEPPING_PUBLISH_DATA.name()) || subjectType.equals(DefaultSubjectType.STEPPING_DATA_ARRIVED.name())) {
           return true;
        }
        return false;
    }

    @Override
    public void newDataArrivedCallBack(ISubject subject, SubjectContainer subjectContainer) {
        if(subject.getType().equals(DefaultSubjectType.STEPPING_PUBLISH_DATA.name())) {
            counterProduce++;
        }

        if(subject.getType().equals(DefaultSubjectType.STEPPING_DATA_ARRIVED.name())) {
           counterConsume++;
        }
    }

    @Override
    public void tickCallBack() {
        System.out.println("InOutCounterDefaultStep **** COUNTER PRODUCE ******* : " + counterProduce + "**** COUNTER CONSUME ******* : " + counterConsume);
        //System.out.println("**** COUNTER CONSUME ******* : " + counterConsume);
      //  throw new RuntimeException("test");
    }

    @Override
    public void restate()  {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shuttingDown() {

    }

    @Override
    public void setContainer(Container cntr) {

    }
}
