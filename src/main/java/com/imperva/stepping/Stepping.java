package com.imperva.stepping;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class Stepping {
    private List<IAlgoDecorator> algos = new CopyOnWriteArrayList<>();
    private HashMap<String, IAlgoDecorator> algosMapToControll = new HashMap<>();

    public Stepping register(Algo iAlgo) {
        IAlgoDecorator algo = new AlgoDecorator(iAlgo);
        algos.add(algo);
        return this;
    }

    public Stepping registerAndControl(String id, Algo iAlgo) {
        if (algosMapToControll.containsKey(id))
            throw new SteppingException("Algo id must be unique. Id: " + id + " already exists");

        IAlgoDecorator algo = new AlgoDecorator(iAlgo);
        algosMapToControll.put(id, algo);
        return this;
    }

    public RemoteControllers go() {

        RemoteControllers remoteControllers = new RemoteControllers();
        if (algos.size() > 0) {
            for (Algo algo : algos) {
                algo.init();
            }
        }

        if (algosMapToControll.size() > 0) {
            Iterator<Map.Entry<String, IAlgoDecorator>> iterator = algosMapToControll.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, IAlgoDecorator> pair = iterator.next();
                pair.getValue().init();

                RemoteController remoteController = new RemoteController();
                remoteController.setCloseable(pair.getValue());
                remoteController.setContainer(pair.getValue().getContainer());
                remoteControllers.add(pair.getKey(), remoteController);
            }
        }
        return remoteControllers;
    }
}