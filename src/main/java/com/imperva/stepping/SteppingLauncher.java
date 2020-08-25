package com.imperva.stepping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SteppingLauncher {
    private String algoName;
    private Algo algo;
    private Object syncObj = new Object();
    private ContainerRegistrar containerRegistrar = new ContainerRegistrar();
    private volatile List<String> subjects = new ArrayList<>();//* todo need?
    private HashMap<String, Data> subjectsStatus = new HashMap<>();


    public SteppingLauncher withAlgo(String algoName, Algo algo) {

        this.algoName = algoName;
        this.algo = algo;
        return this;
    }

    public SteppingLauncher withAlgo(Algo algo) {

        this.algoName = algo.getClass().getName();
        this.algo = algo;
        return this;
    }

    public SteppingLauncher stopOnSubject(String subject) {
        subjects.add(subject);
        for (String sub : subjects) {
            subjectsStatus.put(sub, null);
        }
        return this;
    }

    public SteppingLauncher withStep(Step step, StepConfig stepConfig) {
        StepExternalConfig stepExternalConfig = new StepExternalConfig(step, stepConfig);
        containerRegistrar.add(stepExternalConfig);
        return this;
    }

    public SteppingLauncher withStep(Step step) {
        containerRegistrar.add(step);
        return this;
    }

    public SteppingLauncher withContainerRegistrar(ContainerRegistrar containerRegistrar) {
        this.containerRegistrar = containerRegistrar;
        return this;
    }

    public LauncherResults launch() {
        AlgoDecoratorLauncher algoDecoratorLauncher = new AlgoDecoratorLauncher(algo, containerRegistrar, subjects, this::testingCallbackListener);

        new Stepping()
                .registerAndControl(algoName, algoDecoratorLauncher)
                .go();

        waitTillDone();

        return new LauncherResults(subjectsStatus);
    }


    public void go() {
        AlgoDecoratorLauncher algoDecoratorLauncher = new AlgoDecoratorLauncher(algo, containerRegistrar, subjects, this::testingCallbackListener);

        new Stepping()
                .registerAndControl(algoName, algoDecoratorLauncher)
                .go();
    }

    private void waitTillDone() {
        synchronized (syncObj) {
            try {
                syncObj.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    boolean testingCallbackListener(Data d, String s) {

        subjectsStatus.put(s, d);

        boolean allArrived = isAllArrived();

        if (allArrived) {
            releaseWait();
            return true;
        }
        return false;
    }

    private void releaseWait() {
        synchronized (syncObj) {
            syncObj.notifyAll();
        }
    }

    private boolean isAllArrived() {
        boolean allArrived = true;
        for (Map.Entry<String, Data> entry : subjectsStatus.entrySet()) {
            if (entry.getValue() == null) {
                allArrived = false;
                break;
            }
        }
        return allArrived;
    }
}
