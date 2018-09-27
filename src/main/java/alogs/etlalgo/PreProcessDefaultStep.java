package alogs.etlalgo;


import Stepping.*;
import alogs.etlalgo.converters.EtlTuppleConverter;
import alogs.etlalgo.dto.EtlTupple;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class PreProcessDefaultStep implements Step {

    private EtlTuppleConverter etlTuppleConverter;

    PreProcessDefaultStep() {
       // super(PreProcessDefaultStep.class.getName());
        etlTuppleConverter = new EtlTuppleConverter();
    }

//    public void attach(ISubject iSubject) {
//        if (DefaultSubjectType.S_DATA_ARRIVED.name().equals(iSubject.getType())) {
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
    public StepConfig getStepConfig() {
        return null;
    }

    @Override
    public void init() {

    }

    @Override
    public boolean isAttach(String subjecType) {
        if (DefaultSubjectType.S_DATA_ARRIVED.name().equals(subjecType)) {
           return true;
        }
        return false;
    }

    @Override
    public void newDataArrivedCallBack(ISubject subject, SubjectContainer subjectContainer) {

        if (DefaultSubjectType.S_DATA_ARRIVED.name().equals(subject.getType())) {
            System.out.println("PreProcessDefaultStep: newDataArrivedSubject Arrived!");
            List<JsonObject> data = (List<JsonObject>) subject.getData().getValue();
            List<EtlTupple> tupples = data.stream()
                    .map(jsonObject -> etlTuppleConverter.convert(jsonObject))
                    .collect(Collectors.toList());
            subjectContainer.<List<EtlTupple>>getByName(SubjectType.AGGREGATION.name()).setData(new Data(tupples));
        }
    }
}