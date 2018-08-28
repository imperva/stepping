package alogs.etlalgo;

import alogs.Data;
import alogs.IStep;
import alogs.ISubject;

public class PreProcessStep implements IStep {
    public void init() {

    }

    public void attach(ISubject iSubject) {
      if(iSubject.getData().getType() == "XYZ"){
          iSubject.attach(this);
      }
    }

    public void dataArrived(Data data) {

    }

    public void shutdown() {

    }
}
