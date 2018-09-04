package alogs.etlalgo;

import Stepping.*;

public class AggregationStep extends StepBase {

    protected AggregationStep() {
        super(AggregationStep.class.getName());
    }

    public void attach(ISubject iSubject) {
        if (iSubject.getType().equals(SubjectType.PRE_PROCESS.name())) {
            iSubject.attach(this);
        }
    }

    @Override
    protected void start(ISubject subject, SubjectContainer subjectContainer) {
        //* doing my stuff
        if (subject.getType().equals(SubjectType.PRE_PROCESS.name())) {
            System.out.println("AggregationStep: preProcessedData Arrived!");
            System.out.println("AggregationStep: publishing data");
            publishData(new Data<Object>());
        }
    }
}
