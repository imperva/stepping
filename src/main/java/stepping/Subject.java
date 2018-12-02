package stepping;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Subject implements ISubject {
    private volatile ConcurrentHashMap<SubjectKey, List<IStepDecorator>> iSteps = new ConcurrentHashMap<>();
    private volatile String type;
    private volatile Data data;

    public Subject(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public Data getData() {
        return this.data;
    }

    @Override
    public void publish(Object message) {
        Data data = null;
        if (!(message instanceof Data))
             data = new Data(message);
        else
            data = (Data)message;
        publish(data);
    }


    @Override
    public void publish(Data data) {
        Iterator<Map.Entry<SubjectKey, List<IStepDecorator>>> iterator = iSteps.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<SubjectKey, List<IStepDecorator>> pair = iterator.next();
            pair.getKey().getiDistributionStrategy().distribute(pair.getValue(), data, this.getType());
        }
        this.data = data;
    }

    @Override
    public void attach(IStepDecorator step) {
        IDistributionStrategy distributionStrategy = step.getLocalStepConfig().getDistributionStrategy();
        if (distributionStrategy == null)
            throw new RuntimeException("IDistributionStrategy missing");
        SubjectKey subjectKey = new SubjectKey(step.getDistributionNodeID(), distributionStrategy);
        List<IStepDecorator> distributionList = iSteps.get(subjectKey);
        if (distributionList != null) {
            distributionList.add(step);
        } else {
            List<IStepDecorator> newDistributionList = new ArrayList<>();
            newDistributionList.add(step);
            iSteps.put(subjectKey, newDistributionList);
        }
    }

    private class SubjectKey {
        private final String distributionNodeID;
        private final IDistributionStrategy iDistributionStrategy;

        public SubjectKey(String distributionNodeID, IDistributionStrategy distributionStrategy) {
            this.distributionNodeID = distributionNodeID;
            this.iDistributionStrategy = distributionStrategy;
        }

        @Override
        public boolean equals(Object o) {

            if (o == this) return true;
            if (!(o instanceof SubjectKey)) {
                return false;
            }

            SubjectKey subjectKey = (SubjectKey) o;

            return subjectKey.distributionNodeID.equals(this.distributionNodeID);
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + this.distributionNodeID.hashCode();
            return result;
        }

        public IDistributionStrategy getiDistributionStrategy() {
            return iDistributionStrategy;
        }

        public String getDistributionNodeID() {
            return distributionNodeID;
        }


    }
}
