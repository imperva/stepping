package stepping;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Subject implements ISubject {
    private ConcurrentHashMap<SubjectKey, List<IStepDecorator>> iSteps = new ConcurrentHashMap<>();
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private String type;
    private Data data;

    //todo why needed?
    private Container cntr;

    public Subject(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public Data getData() {
        Data d = null;
        readWriteLock.readLock().lock();
        d = this.data;
        readWriteLock.readLock().unlock();
        return d;
    }

    @Override
    public void publish(Data data) {
        data.setSubjectType(this.type);
        Iterator<Map.Entry<SubjectKey, List<IStepDecorator>>> iterator = iSteps.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<SubjectKey, List<IStepDecorator>> pair = iterator.next();
            pair.getKey().getiDistributionStrategy().distribute(pair.getValue(), data);
        }

        readWriteLock.writeLock().lock();
        this.data = data;
        readWriteLock.writeLock().unlock();
    }

    @Override
    public void attach(IStepDecorator step) {
        SubjectKey subjectKey = new SubjectKey(step.getDistributionNodeID(), step.getLocalStepConfig().getDistributionStrategy());
        List<IStepDecorator> distributionList = iSteps.get(subjectKey);
        if (distributionList != null) {
            distributionList.add(step);
        } else {
            List<IStepDecorator> newDistributionList = new ArrayList<>();
            newDistributionList.add(step);
            iSteps.put(subjectKey, newDistributionList);
        }
    }

    @Override
    public Container getContainer() {
        return cntr;
    }

    @Override
    public void setContainer(Container container) {
        this.cntr = container;
    }

    class SubjectKey{
        private String distributionNodeID;
        private IDistributionStrategy iDistributionStrategy;

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

        //Idea from effective Java : Item 9
        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + this.distributionNodeID.hashCode();

            return result;
        }

        public IDistributionStrategy getiDistributionStrategy() {
            return iDistributionStrategy;
        }

        public void setiDistributionStrategy(IDistributionStrategy iDistributionStrategy) {
            this.iDistributionStrategy = iDistributionStrategy;
        }

        public String getDistributionNodeID() {
            return distributionNodeID;
        }

        public void setDistributionNodeID(String distributionNodeID) {
            this.distributionNodeID = distributionNodeID;
        }
    }
}
