package stepping;

import java.util.*;

public class Subject implements ISubject {
    private HashMap<String, List<IStepDecorator>> iSteps = new HashMap<String, List<IStepDecorator>>();

    private String type;
    private Data data;
    private Container cntr;
    private AllDistributionPolicy allDistributionPolicy = new AllDistributionPolicy();
    private EvenDistributionPolicy evenDistributionPolicy = new EvenDistributionPolicy();

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
            if (pair.getKey().equals("global")) {
                allDistributionPolicy.distribute(pair.getValue(), this.data);
            } else {
                evenDistributionPolicy.distribute(pair.getValue(), this.data);
            }
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
        this.data = data;
        this.data.setSubjectType(this.type);
        publish();
    }

    @Override
    public Container getContainer() {
        return cntr;
    }

    @Override
    public void setContainer(Container container) {
        this.cntr = container;
    }


    public interface DistributionPolicy {
        void distribute(List<IStepDecorator> iStepDecorators, Data data);
    }

    public class AllDistributionPolicy implements DistributionPolicy {
        @Override
        public void distribute(List<IStepDecorator> iStepDecorators, Data data) {
            for (IStepDecorator step : iStepDecorators) {
                step.newDataArrived(data);
            }
        }
    }

    public class EvenDistributionPolicy implements DistributionPolicy {
        @Override
        public void distribute(List<IStepDecorator> iStepDecorators, Data data) {
            try {

                if (!(data.getValue() instanceof List))
                    throw new RuntimeException("EvenDistributionPolicy not supported");

                List dataToDistribute = ((List) getData().getValue());
                int chunks = Math.floorDiv(dataToDistribute.size(), iStepDecorators.size());

                List<List> chopped = chopped(dataToDistribute, chunks);
                if (chopped.size() > iStepDecorators.size()) {
                    chopped.get(chopped.size() - 2).addAll(chopped.get(chopped.size() - 1));
                    chopped.remove(chopped.size() - 1);
                }
                for (int u = 0; u < iStepDecorators.size(); u++) {
                    iStepDecorators.get(u).newDataArrived(new Data(chopped.get(u), type));
                }
            } catch (Exception e) {
                System.out.println("");
            }

        }
    }

    //todo - very expensive - fix
    static <T> List<List<T>> chopped(List<T> list, final int L) {
        List<List<T>> parts = new ArrayList<List<T>>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<T>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }
}
