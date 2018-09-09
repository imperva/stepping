package alogs.etlalgo;

import Stepping.*;

public class AggregationStep extends StepBase {

    protected AggregationStep() {
        super(AggregationStep.class.getName());
    }

    @Override
    public void attach(ISubject iSubject) {
        if (iSubject.getType().equals(SubjectType.PRE_PROCESS.name())) {
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

    @Override
    protected void newDataArrivedCallBack(ISubject subject, SubjectContainer subjectContainer) {
        //* doing my stuff
        if (subject.getType().equals(SubjectType.PRE_PROCESS.name())) {
            System.out.println("AggregationStep: preProcessedData Arrived!");
            System.out.println("AggregationStep: publishing data");
            publishData(new Data<Object>());
        }
    }
}
