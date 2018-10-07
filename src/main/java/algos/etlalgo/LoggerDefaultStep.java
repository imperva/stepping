package algos.etlalgo;

import Stepping.*;

public class LoggerDefaultStep implements Step {
    LoggerDefaultStep() {
    }

    @Override
    public void init() {

    }

    @Override
    public boolean followsSubject(String subjectType) {
        return true;
    }

    @Override
    public void newDataArrivedCallBack(Data data, SubjectContainer subjectContainer) {
        System.out.println("**** Logged event ******* : " + data.getSubjectType());
        //throw new RuntimeException("TEST");
    }

    @Override
    public void tickCallBack() {
        System.out.println("LoggerDefaultStep");
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
