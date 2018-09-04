package alogs.etlalgo;

import Stepping.ISubject;
import Stepping.StepBase;
import Stepping.SubjectContainer;

public class LoggerStep extends StepBase {
    protected LoggerStep() {
        super(LoggerStep.class.getName());
    }

    @Override
    protected void start(ISubject data, SubjectContainer subjectContainer) {
        System.out.println("LISTEN TO ALLLLLLLLLLLLLLLLLLL: " + data.getType());
    }

    @Override
    public void attach(ISubject iSubject) {
        iSubject.attach(this);
    }
}
