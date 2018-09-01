package alogs.etlalgo;


import Stepping.*;

public class PreProcessStep extends StepBase {

    protected PreProcessStep() { super("", 1, 1);

    }

    @Override
    public void init() {

    }

    public void attach(ISubject iSubject) {
        if (iSubject.getType() == "xyz") {
            iSubject.attach(this);
        }
    }

    @Override
    public void dataArrived(ISubject subject, SubjectContainer subjectContainer) {
        if (subject.getType() == "xyz") {
          //DO SOMETHING
        }
    }

    public void dataArrived(Data data) {

    }

    public void shutdown() {

    }

    @Override
    public void run() {

    }
}
