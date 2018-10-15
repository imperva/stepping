package stepping;

import java.util.ArrayList;
import java.util.List;

public class Stepping {
    private List<Algo> algos = new ArrayList<Algo>();

    public Stepping register(Algo iAlgo, IMessenger iMessenger) {
        IAlgoDecorator algo = new DefaultAlgoDecorator(iAlgo);
        algos.add(algo);
        algo.setMessenger(iMessenger);
        return this;
    }

    public void go() {
        for (Algo algo : algos) {
            algo.init();
        }
    }
}