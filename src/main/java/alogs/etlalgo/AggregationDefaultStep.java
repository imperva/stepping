package alogs.etlalgo;

import Stepping.*;
import alogs.etlalgo.dto.EtlTupple;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AggregationDefaultStep implements Step {

    private Gson gson;

    public AggregationDefaultStep() {
        //super(AggregationDefaultStep.class.getName());
        gson = new Gson();
    }

//    @Override
//    public void attach(ISubject iSubject) {
//        if (iSubject.getType().equals(SubjectType.AGGREGATION.name())) {
//            iSubject.attach(this);
//        }
//    }

//    @Override
//    public void shutdown() {
//
//    }

    @Override
    public void tickCallBack() {
        System.out.println("AggregationDefaultStep TICKS");
    }

    @Override
    public void restate()  {
        try {
            Thread.sleep(1);
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
    public boolean isAttach(String subjectType) {
        if (subjectType.equals(SubjectType.AGGREGATION.name())) {
            return true;
        }
        return false;
    }

    //todo add subjectContainer.getByName(DefaultSubjectType.S_PUBLISH_DATA.name()).setData(aggrTupples); to SubjectContainer?
    @Override
    public void newDataArrivedCallBack(ISubject subject, SubjectContainer subjectContainer) {
        //* doing my stuff
        if (subject.getType().equals(SubjectType.AGGREGATION.name())) {
            System.out.println("AggregationDefaultStep: preProcessedData Arrived!");
            System.out.println("AggregationDefaultStep: publishing data");
            List<EtlTupple> tupples = (List<EtlTupple>) subject.getData().getValue();
            List<JsonObject> aggrTupples = tupples.stream()
                    .distinct()
                    .map(etlTupple -> gson.toJsonTree(etlTupple).getAsJsonObject())
                    .collect(Collectors.toList());
            subjectContainer.getByName(DefaultSubjectType.S_PUBLISH_DATA.name()).setData(new Data(aggrTupples));
        }
    }
}
