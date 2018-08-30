package alogs.etlalgo;

import Stepping.AlgoBase;
import Stepping.Data;
import Stepping.container.Container;

public class ETLAlgo extends AlgoBase {

    public ETLAlgo(){ super(ETLAlgo.class.getName(),1,1);}

    @Override
    public void start() {

        Container cntr = Container.getInstance();
        //* in thread
        getSubjectContainer().getByName("newDataArrivedSubject").setData(new Data("sss"));
    }

    @Override
    protected void IoC() {

        //* init subjects
        DI(new PreProcessSubject(), "preProcessSubject");


        //* init steps
        DI(new PreProcessStep(), "preStep");
        DI(new AggregationStep(), "aggreStep");
    }



    public void shutdown() {
        //* cleanup;
    }
}