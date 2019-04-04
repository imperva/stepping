package com.imperva.stepping;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Stepping {
    private List<Algo> algos = new CopyOnWriteArrayList<>();

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