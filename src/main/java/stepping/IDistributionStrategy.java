package stepping;

import java.util.List;

public interface IDistributionStrategy {
    void distribute(List<IStepDecorator> steps, Data data);
}
