package algos.etlalgo;


import Stepping.*;
import algos.etlalgo.converters.EtlTuppleConverter;
import algos.etlalgo.dto.EtlTuple;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

public class PreProcessDefaultStep implements Step {

    private EtlTuppleConverter etlTuppleConverter;

    PreProcessDefaultStep() {
       // super(PreProcessDefaultStep.class.getName());
        etlTuppleConverter = new EtlTuppleConverter();
    }

//    public void attach(ISubject iSubject) {
//        if (DefaultSubjectType.STEPPING_DATA_ARRIVED.name().equals(iSubject.getType())) {
//            iSubject.attach(this);
//        }
//    }



    @Override
    public void tickCallBack() {
        System.out.println("PreProcessDefaultStep TICKS");
    }

    @Override
    public void restate() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shuttingDown() {

    }

    @Override
    public void setContainer(Container cntr) {

    }

    @Override
    public void init() {

    }

    @Override
    public boolean followsSubject(String subjectType) {
        if (DefaultSubjectType.STEPPING_DATA_ARRIVED.name().equals(subjectType)) {
           return true;
        }
        return false;
    }

    @Override
    public void newDataArrivedCallBack(Data data, SubjectContainer subjectContainer) {
        if (DefaultSubjectType.STEPPING_DATA_ARRIVED.name().equals(data.getSubjectType())) {
            System.out.println("PreProcessDefaultStep: newDataArrivedSubject Arrived!");
            List<EtlTuple> tupples = ((List<JsonObject>)data.getValue()).stream()
                    .map(jsonObject -> etlTuppleConverter.convert(jsonObject))
                    .collect(Collectors.toList());
            subjectContainer.<List<EtlTuple>>getByName(SubjectType.AGGREGATION.name()).setData(new Data(tupples));
        }
    }

//    @Override
//    public StepConfig getLocalStepConfig(){
//        StepConfig stepConfig = new StepConfig();
//        stepConfig.setNumOfNodes(4);
//        return stepConfig;
//    }

}