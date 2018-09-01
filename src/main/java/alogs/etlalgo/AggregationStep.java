package alogs.etlalgo;

import Stepping.*;

public class AggregationStep extends StepBase {

    protected AggregationStep() { super("", 1, 1);

    }

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

    @Override
    public void run() {

    }
}
