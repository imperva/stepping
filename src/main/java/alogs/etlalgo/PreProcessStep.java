package alogs.etlalgo;


import alogs.Data;
import alogs.IStep;
import alogs.ISubject;
import alogs.SubjectContainer;

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
