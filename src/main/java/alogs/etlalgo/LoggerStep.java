package alogs.etlalgo;

import Stepping.ISubject;
import Stepping.StepBase;
import Stepping.SubjectContainer;

public class LoggerStep extends StepBase {
    protected LoggerStep() {
        super(LoggerStep.class.getName());
    }

    @Override
    public void attach(ISubject iSubject) {
        iSubject.attach(this);
    }

    @Override
    public void shutdown() {

    }

    @Override
    protected void newDataArrivedCallBack(ISubject subject, SubjectContainer subjectContainer) {
        System.out.println("LISTEN TO ALLLLLLLLLLLLLLLLLLL: " + subject.getType());
    }

    @Override
    protected void tickCallBack() {
        System.out.println("LoggerStep TICKS");
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
