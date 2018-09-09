package Stepping;

import java.util.ArrayList;
import java.util.List;

public class Stepping {
    private List<IAlgo> algos = new ArrayList<IAlgo>();

    public Stepping register(IAlgo iAlgo, IMessenger iMessenger) {
        algos.add(iAlgo);
        iAlgo.setMessenger(iMessenger);
        return this;
    }

    public void go() {
        for (IAlgo algo : algos) {
            algo.init();
        }
    }
}