package stepping;

import java.util.List;

public interface IDistributionStrategy {
    //todo StepDecorator should not be exposed to API consumers
    void distribute(List<IStepDecorator> steps, Data data, String subjectType);
}
