package alogs.etlalgo;

import Stepping.Data;
import Stepping.IStep;
import Stepping.ISubject;
import Stepping.SubjectContainer;

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
            subjectContainer.getByName("ImessgaeHandler").setData(data);
        }
    }


    public void shutdown() {

    }

}
