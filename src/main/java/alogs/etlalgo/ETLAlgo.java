package alogs.etlalgo;

import Stepping.AlgoBase;
import Stepping.Data;
import Stepping.ISubject;
import Stepping.Subject;
import Stepping.container.Container;

public class ETLAlgo extends AlgoBase {

    public ETLAlgo(){ super(ETLAlgo.class.getName(),1,1);}

    @Override
    public void start(Data data) {
        Container cntr = Container.getInstance();
        //* in thread
        getSubjectContainer().getByName("newDataArrivedSubject").setData(data);
    }

    @Override
    protected void IoC() {
        //* init subjects
        ISubject subject = new Subject();
        subject.setType("preProcessSubject");

        DI(subject, "preProcessSubject");

        //* init steps
        DI(new PreProcessStep(), "preStep");
        DI(new AggregationStep(), "aggreStep");
    }

    public void shutdown() {
        //* cleanup;
    }
}