package alogs.etlalgo;


import Stepping.*;
import Stepping.defaultsteps.DefaultSubjectType;
import alogs.etlalgo.converters.EtlTuppleConverter;
import alogs.etlalgo.dto.EtlTupple;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

public class PreProcessStep extends StepBase {

    private EtlTuppleConverter etlTuppleConverter;

    protected PreProcessStep() {
        super(PreProcessStep.class.getName());
        etlTuppleConverter = new EtlTuppleConverter();
    }

    public void attach(ISubject iSubject) {
        if (DefaultSubjectType.S_DATA_ARRIVED.name().equals(iSubject.getType())) {
            iSubject.attach(this);
        }
    }

    @Override
    public void shutdown() {

    }

    @Override
    protected void tickCallBack() {
        System.out.println("PreProcessStep TICKS");
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
    protected void newDataArrivedCallBack(ISubject subject, SubjectContainer subjectContainer) {
        if (DefaultSubjectType.S_DATA_ARRIVED.name().equals(subject.getType())) {
            System.out.println("PreProcessStep: newDataArrivedSubject Arrived!");
            Data<List<JsonObject>> data = subject.getData();
            List<EtlTupple> tupples = data.getValue().stream()
                    .map(jsonObject -> etlTuppleConverter.convert(jsonObject))
                    .collect(Collectors.toList());
            subjectContainer.getByName(SubjectType.AGGREGATION.name()).setData(new Data(tupples));
        }
    }
}