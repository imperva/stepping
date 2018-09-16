package alogs.dbusertodbprofilealgo;

import Stepping.AlgoBase;
import Stepping.Data;

public class DbUserAccessDbProfilerAlgo extends AlgoBase {

    public DbUserAccessDbProfilerAlgo() {
        super(DbUserAccessDbProfilerAlgo.class.getName());
    }

    @Override
    protected void attachExternalDataReceiver() {
        //Set externalDataReceiver if we want to use orchestrator to manage the flow
//        ExternalDataConsumerStep externalDataConsumerStep = getContainer().getById(DefaultID.EXTERNAL_DATA_CONSUMER.name());
//        if (externalDataConsumerStep != null) {
//            externalDataConsumerStep.setExternalDataReceiver(this);
//        }
    }

    @Override
    protected void newDataArrivedCallBack(Data data) {
//         getSubjectContainer().getByName(DefaultSubjectType.S_DATA_ARRIVED.name()).setData(data);
    }

    @Override
    protected void tickCallBack() {

    }

    @Override
    public void newDataArrived(Data<?> data) {
//        q.queue(data);
    }

    @Override
    protected void IoC() {
        super.IoC();

        //* init steps
        DI(new LearningStep(), "learningStep");
    }
}
