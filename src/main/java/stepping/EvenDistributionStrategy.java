package stepping;

import java.util.ArrayList;
import java.util.List;

public class EvenDistributionStrategy implements IDistributionStrategy {
    @Override
    public void distribute(List<IStepDecorator> iStepDecorators, Data data, String subjectType) {
        if (!(data.getValue() instanceof List))
            throw new RuntimeException("EvenDistributionStrategy not supported");

        List dataToDistribute = ((List) data.getValue());
        int chunks = Math.floorDiv(dataToDistribute.size(), iStepDecorators.size());

        List<List> chopped = chopped(dataToDistribute, chunks);
        if (chopped.size() > iStepDecorators.size()) {
            chopped.get(chopped.size() - 2).addAll(chopped.get(chopped.size() - 1));
            chopped.remove(chopped.size() - 1);
        }
        for (int u = 0; u < iStepDecorators.size(); u++) {
            iStepDecorators.get(u).newDataArrived(new Data(chopped.get(u)), subjectType);
        }
    }

    //todo - very expensive - fix
    private  <T> List<List<T>> chopped(List<T> list, final int L) {
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
