package alogs.etlalgo;

import alogs.*;

import java.util.Queue;

public class AggregationStep implements IStep {

    public void init() {

    }

    public void attach(ISubject iSubject) {
        if (iSubject.getData().getType() == "123") {

        }
    }

    public void dataArrived(Data data, SubjectContainer subjectContainer) {
        //* doing my stuff
        if (data.getType() == "publisgdecided") {
            subjectContainer.getByName("ImessgaeHandler").occurred(data);
        }
    }


    public void shutdown() {

    }

}
