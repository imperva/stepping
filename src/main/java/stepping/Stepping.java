package stepping;

import java.util.ArrayList;
import java.util.List;

public class Stepping {
    private List<Algo> algos = new ArrayList<Algo>();

    public Stepping register(Algo iAlgo) {
        IAlgoDecorator algo = new AlgoDecorator(iAlgo);
        algos.add(algo);

        return this;
    }

    public void go() {
        for (Algo algo : algos) {
            algo.init();
        }
    }
}