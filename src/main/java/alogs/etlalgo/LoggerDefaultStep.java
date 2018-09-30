package alogs.etlalgo;

import Stepping.*;

public class LoggerDefaultStep implements Step {
    LoggerDefaultStep() {
    }

    @Override
    public void init() {

    }

    @Override
    public boolean isAttach(String subjectType) {
        return true;
    }

    @Override
    public void newDataArrivedCallBack(ISubject subject, SubjectContainer subjectContainer) {
        System.out.println("**** Logged event ******* : " + subject.getType());
        //throw new RuntimeException("TEST");
    }

    @Override
    public void tickCallBack() {
        System.out.println("LoggerDefaultStep");
    }

    @Override
    public void restate()  {
        try {
            Thread.sleep(5000);
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

    @Override
    public StepConfig getLocalStepConfig(){
        return null;
    }
}
