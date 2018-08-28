package alogs.etlalgo;

import alogs.Data;
import alogs.IStep;
import alogs.ISubject;

public class AggregationStep implements IStep {
    public void init() {

    }

    public void attach(ISubject iSubject) {
      if(iSubject.getData().getType() == "123"){

      }
    }

    public void dataArrived(Data data) {

    }

    public void shutdown() {

    }
}
