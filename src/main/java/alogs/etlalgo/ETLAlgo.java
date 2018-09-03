package alogs.etlalgo;

import Stepping.AlgoBase;
import Stepping.Data;
import Stepping.ISubject;
import Stepping.Subject;

public class ETLAlgo extends AlgoBase {

    public ETLAlgo() {
        super(ETLAlgo.class.getName());
    }

    @Override
    public void start(Data data) {

        //* in thread
         getSubjectContainer().getByName("newDataArrivedSubject").setData(data);
    }

    @Override
    protected void IoC() {
        //* init subjects
        ISubject<PreProcessDTO> subject = new Subject<PreProcessDTO>(SubjectType.PRE_PROCESS.name());
        DI(subject, "preProcessedData");

        //* init steps
        DI(new PreProcessStep(), "preStep");
        DI(new AggregationStep(), "aggreStep");
    }

    public void shutdown() {
        //* cleanup;
    }
}