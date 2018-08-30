package Stepping;

import Stepping.container.Container;

import java.util.ArrayList;
import java.util.List;

public class Stepping {
    private List<IAlgo> algos = new ArrayList<IAlgo>();

    public Stepping add(IAlgo iAlgo) {
        algos.add(iAlgo);
        return this;
    }

    public void init(){
        for (IAlgo algo : algos) {
            algo.init();
        }
    }
}
