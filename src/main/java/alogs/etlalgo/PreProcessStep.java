package alogs.etlalgo;


import Stepping.Data;
import Stepping.IStep;
import Stepping.ISubject;
import Stepping.SubjectContainer;

public class PreProcessStep implements IStep {
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
}
