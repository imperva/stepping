package com.imperva.stepping;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Stepping {
    private List<Algo> algos = new CopyOnWriteArrayList<>();
    private SteppingConfig steppingConfig = new SteppingConfig();

    public Stepping() {
    }

    public Stepping(SteppingConfig steppingConfig) {
        this.steppingConfig = steppingConfig;
    }

    public Stepping register(Algo iAlgo) {
        IAlgoDecorator algo = new AlgoDecorator(iAlgo);
        algos.add(algo);
        algo.setSteppingConfig(steppingConfig);
        return this;
    }

    public void go() {
        for (Algo algo : algos) {
            algo.init();
        }
    }
}