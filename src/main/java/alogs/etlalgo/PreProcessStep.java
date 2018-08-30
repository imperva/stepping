package alogs.etlalgo;


import Stepping.Data;
import Stepping.IStep;
import Stepping.ISubject;
import Stepping.SubjectContainer;

public class PreProcessStep implements IStep {
    public void init() {

    }

    public void attach(ISubject iSubject) {
      if(iSubject.getData().getType() == "XYZ"){
          iSubject.attach(this);
      }
    }

    @Override
    public void dataArrived(Data data, SubjectContainer subjectContainer) {

    }

    public void dataArrived(Data data) {

    }

    public void shutdown() {

    }
}
