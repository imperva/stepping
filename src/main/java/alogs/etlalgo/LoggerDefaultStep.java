package alogs.etlalgo;

import Stepping.*;

import java.io.IOException;

public class LoggerDefaultStep implements Step {

    LoggerDefaultStep() {

        //super(LoggerDefaultStep.class.getName());
    }
    private long counterProduce;
    private long counterConsume;



    @Override
    public void init() {

    }

    @Override
    public boolean isAttach(String subjectType) {
        if(subjectType.equals(DefaultSubjectType.S_PUBLISH_DATA.name()) || subjectType.equals(DefaultSubjectType.S_DATA_ARRIVED.name())) {
           return true;
        }
        return false;
    }

    @Override
    public void newDataArrivedCallBack(ISubject subject, SubjectContainer subjectContainer) {
        if(subject.getType().equals(DefaultSubjectType.S_PUBLISH_DATA.name())) {
            counterProduce++;
        }

        if(subject.getType().equals(DefaultSubjectType.S_DATA_ARRIVED.name())) {
           counterConsume++;
        }
    }

    @Override
    public void tickCallBack() {
        System.out.println("**** COUNTER PRODUCE ******* : " + counterProduce);
        System.out.println("**** COUNTER CONSUME ******* : " + counterConsume);
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
    public StepConfig getStepConfig() {
        StepConfig stepConfig = new StepConfig();
        stepConfig.setDecelerationStrategy(new DefaultDecelerationStrategy());
        return stepConfig;
    }
}
