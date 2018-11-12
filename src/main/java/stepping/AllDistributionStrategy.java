package stepping;

import java.util.List;

public class AllDistributionStrategy implements IDistributionStrategy {
    @Override
    public void distribute(List<IStepDecorator> iStepDecorators, Data data, String subjectType) {
        for (IStepDecorator step : iStepDecorators) {
            step.newDataArrived(data, subjectType);
        }
    }
}