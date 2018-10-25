package stepping;

import java.io.Closeable;

public interface IStepDecorator extends Step, Closeable {

    void newDataArrived(Data data);

    void attach(ISubject iSubject);

    Step getStep();

    void setGlobalAlgoStepConfig(GlobalAlgoStepConfig globalAlgoStepConfig);

    void setDistributionNodeID(String name);

    String getDistributionNodeID();

    void dataListener();

    void tickCallBackThreadSafe();

    void dataListenerThreadSafe();
}
