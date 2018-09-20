package alogs.etlalgo;

import Stepping.ISubject;
import Stepping.StepBase;
import Stepping.SubjectContainer;
import Stepping.defaultsteps.DefaultSubjectType;

public class LoggerStep extends StepBase {

    LoggerStep() {
        super(LoggerStep.class.getName());
    }
    private long counterProduce;
    private long counterConsume;

    @Override
    public void attach(ISubject iSubject) {
        if(iSubject.getType().equals(DefaultSubjectType.S_PUBLISH_DATA.name()) || iSubject.getType().equals(DefaultSubjectType.S_DATA_ARRIVED.name())) {
            iSubject.attach(this);
        }
    }

    @Override
    public void shutdown() {

    }

    @Override
    protected void newDataArrivedCallBack(ISubject subject, SubjectContainer subjectContainer) {
        if(subject.getType().equals(DefaultSubjectType.S_PUBLISH_DATA.name())) {
            counterProduce++;
        }

        if(subject.getType().equals(DefaultSubjectType.S_DATA_ARRIVED.name())) {
           counterConsume++;
        }
    }

    @Override
    protected void tickCallBack() {
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
}
