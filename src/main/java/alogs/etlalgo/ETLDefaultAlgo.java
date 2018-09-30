package alogs.etlalgo;

import Stepping.*;
import alogs.etlalgo.dto.EtlId;
import java.util.HashMap;

public class ETLDefaultAlgo implements Algo {

    public ETLDefaultAlgo() {
       // super(ETLDefaultAlgo.class.getName());
    }

    @Override
    public void tickCallBack(){
        System.out.println("ETLDefaultAlgo TICKS");
    }

    @Override
    public StepConfig getGlobalAlgoStepConfig() {
        StepConfig stepConfig = new StepConfig();
        stepConfig.setEnableDecelerationStrategy(false);
        return stepConfig;
    }

    @Override
    public void init() {

    }

    @Override
    public void setMessenger(IMessenger messenger) {

    }

    @Override
    public HashMap<String, Object> IoC() {
        //super.IoC();

        HashMap<String,java.lang.Object> iocMap = new HashMap<>();

        //* init subjects
        ISubject subject = new Subject(SubjectType.AGGREGATION.name());
        iocMap.put(subject.getType(),subject);

        //* init steps
        iocMap.put(EtlId.PRE_STEP, new PreProcessDefaultStep());
        iocMap.put(EtlId.AGGREGATION_STEP, new AggregationDefaultStep());
        iocMap.put(EtlId.LOGGER_STEP,new LoggerDefaultStep());
        iocMap.put(EtlId.COUNTER_STEP,new InOutCounterDefaultStep());
        return iocMap;
    }


    @Override
    public void close() {

    }


}