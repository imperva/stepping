package alogs.etlalgo;

import Stepping.Data;
import Stepping.IStep;
import Stepping.ISubject;
import Stepping.SubjectContainer;

public class AggregationStep implements IStep {

    public void init() {

    }

    public void attach(ISubject iSubject) {
        if (iSubject.getType() == "123") {

        }
    }

    public void dataArrived(ISubject subject, SubjectContainer subjectContainer) {
        //* doing my stuff
        if (subject.getType() == "publisgdecided") {
            subjectContainer.getByName("ImessgaeHandler").setData(subject.getData());
        }
    }


    public void shutdown() {

    }

}
