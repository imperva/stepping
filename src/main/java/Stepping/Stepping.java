package Stepping;

import java.util.ArrayList;
import java.util.List;

public class Stepping {
    private List<IAlgo> algos = new ArrayList<IAlgo>();

    public Stepping register(IAlgo iAlgo) {
        algos.add(iAlgo);
        iAlgo.init();
        return this;
    }
}
