package alogs.etlalgo;

import Stepping.AlgoBase;
import Stepping.Data;
import Stepping.ISubject;
import Stepping.Subject;
import alogs.etlalgo.dto.EtlId;
import alogs.etlalgo.dto.EtlTupple;

public class ETLAlgo extends AlgoBase {

    public ETLAlgo() {
        super(ETLAlgo.class.getName());
    }

    @Override
    protected void tickCallBack(){
        System.out.println("ETLAlgo TICKS");
    }

    @Override
    protected void IoC() {
        super.IoC();

        //* init subjects
        ISubject<EtlTupple> subject = new Subject<EtlTupple>(SubjectType.AGGREGATION.name());
        DI(subject, subject.getType());

        //* init steps
        DI(new PreProcessStep(), EtlId.PRE_STEP);
        DI(new AggregationStep(), EtlId.AGGREGATION_STEP);
        DI(new LoggerStep(), EtlId.LOGGER_STEP);
    }


    public void shutdown() {
        //* cleanup;
    }
}