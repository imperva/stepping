package com.imperva.stepping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class SteppingTestingToolkit {
    private String algoName;
    private Algo algo;
    private BiFunction<Data, String, Data> function;
    private Object syncObj = new Object();

    Data res = null;
    List<String> subjects = new ArrayList<>();
    HashMap<String, Data> subjectsStatus = new HashMap<>();
    private ContainerRegistrar containerRegistrar;
    private StepConfig stepConfig;


    public SteppingTestingToolkit withAlgo(String algoName, Algo algo) {

        this.algoName = algoName;
        this.algo = algo;
        return this;
    }


    public SteppingTestingToolkit withSubject(String subject) {

        subjects.add(subject);
        for (String sub :
                subjects) {
            subjectsStatus.put(sub,null);

        }
        return this;
    }

    public SteppingTestingToolkit withContainerRegistrar(ContainerRegistrar containerRegistrar) {
        this.containerRegistrar = containerRegistrar;
        return this;
    }

    public SteppingTestingToolkit withStepConfig(StepConfig stepConfig) {
        this.stepConfig = stepConfig;
        return this;
    }

    public  HashMap<String, Data> test() {
        AlgoDecoratorTesting algoDecoratorTesting = new AlgoDecoratorTesting(algo, containerRegistrar, stepConfig, subjects, this::f);
        new Stepping().registerAndControl(algoName, algoDecoratorTesting).go();

        synchronized (syncObj) {
            try {
                syncObj.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return subjectsStatus;
    }

    boolean f(Data d, String s) {

        subjectsStatus.put(s,d);

        boolean allArrived = true;
        for (Map.Entry<String, Data> entry : subjectsStatus.entrySet()) {
           if(entry.getValue() == null) {
               allArrived = false;
               break;

           }

        }

        if(allArrived) {
            System.out.println("Notifying");


            synchronized (syncObj) {
                syncObj.notifyAll();
            }

            res = d;

        return true;
        }

        return false;
    }

}
