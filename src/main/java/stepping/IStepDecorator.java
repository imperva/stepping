package stepping;



import java.io.Closeable;

public interface IStepDecorator extends Step, Closeable {

    void init(Container cntr);

    default void onTickCallBack() {
        throw new RuntimeException("onTickCallBack not implemented");
    }

    void queueSubjectUpdate(Data data, String subjectType);

    void attachTo(ISubject iSubject);

    Step getStep();

    void setAlgoConfig(AlgoConfig algoConfig);

    void setDistributionNodeID(String name);

    String getDistributionNodeID();

    void openDataSink();

}
