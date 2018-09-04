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
    public void newDataArrivedCallBack(Data data) {
        //* in thread
         getSubjectContainer().getByName("newDataArrivedSubject").setData(data);
    }

    @Override
    protected void tickCallBack(){
        System.out.println("ETLAlgo TICKS");
    }


    @Override
    protected void IoC() {
        //* init subjects
        ISubject<PreProcessDTO> subject = new Subject<PreProcessDTO>(SubjectType.PRE_PROCESS.name());
        DI(subject, "preProcessedData");
        DI(new Subject("newDataArrivedSubject"), "newDataArrivedSubject");

        //* init steps
        DI(new PreProcessStep(), "preStep");
        DI(new AggregationStep(), "aggreStep");
        DI(new LoggerStep(), "LoggerStep");

    }


    public void shutdown() {
        //* cleanup;
    }
}