package alogs.etlalgo;

import Stepping.Data;
import Stepping.ISubject;
import Stepping.StepBase;
import Stepping.SubjectContainer;
import Stepping.defaultsteps.DefaultSubjectType;
import alogs.etlalgo.dto.EtlTupple;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

public class AggregationStep extends StepBase {

    private Gson gson;

    protected AggregationStep() {
        super(AggregationStep.class.getName());
        gson = new Gson();
    }

    @Override
    public void attach(ISubject iSubject) {
        if (iSubject.getType().equals(SubjectType.AGGREGATION.name())) {
            iSubject.attach(this);
        }
    }

    @Override
    public void shutdown() {

    }

    @Override
    protected void tickCallBack() {
        System.out.println("AggregationStep TICKS");
    }

    @Override
    public void restate()  {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //todo add subjectContainer.getByName(DefaultSubjectType.S_PUBLISH_DATA.name()).setData(aggrTupples); to SubjectContainer?
    @Override
    protected void newDataArrivedCallBack(ISubject subject, SubjectContainer subjectContainer) {
        //* doing my stuff
        if (subject.getType().equals(SubjectType.AGGREGATION.name())) {
            System.out.println("AggregationStep: preProcessedData Arrived!");
            System.out.println("AggregationStep: publishing data");
            Data<List<EtlTupple>> tupples = subject.getData();
            Data<List<JsonObject>> aggrTupples = new Data(tupples.getValue().stream()
                    .distinct()
                    .map(etlTupple -> gson.toJsonTree(etlTupple))
                    .collect(Collectors.toList()));
            subjectContainer.getByName(DefaultSubjectType.S_PUBLISH_DATA.name()).setData(aggrTupples);
        }
    }
}
