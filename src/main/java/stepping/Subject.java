package stepping;

import java.util.*;

public class Subject implements ISubject {
    private HashMap<String, List<IStepDecorator>> iSteps = new HashMap<String, List<IStepDecorator>>();

    private String type;
    private Data data;
    private Container cntr;
    private Object locker = new Object();

    public Subject(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public Data getData() {
        return data;
    }

    @Override
    public void publish() {
        Iterator<Map.Entry<String, List<IStepDecorator>>> iterator = iSteps.<String, List<IStepDecorator>>entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<IStepDecorator>> pair = iterator.next();
            StepConfig stepConfig =  pair.getValue().get(0).getLocalStepConfig();
            stepConfig.getDistributionStrategy().distribute(pair.getValue(), this.data);
        }
    }

    @Override
    public void attach(IStepDecorator step) {
        List<IStepDecorator> distributionList = iSteps.get(step.getDistributionNodeID());
        if (distributionList != null) {
            distributionList.add(step);
        } else {
            List<IStepDecorator> newDistributionList = new ArrayList<IStepDecorator>();
            newDistributionList.add(step);
            iSteps.put(step.getDistributionNodeID(), newDistributionList);
        }
    }

    @Override
    public void setData(Data data) {
        synchronized (locker) {
            this.data = data;
            this.data.setSubjectType(this.type);
            publish();
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
}
