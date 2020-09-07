package com.imperva.stepping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SteppingLauncher {
    private final Logger logger = LoggerFactory.getLogger(SteppingLauncher.class);
    private String algoName;
    private Algo algo;
    private final Object syncObj = new Object();
    private ContainerRegistrar containerRegistrar = new ContainerRegistrar();
    private volatile List<String> subjects = new ArrayList<>();//* todo need volatile?
    private HashMap<String, Data> subjectsStatus = new HashMap<>();
    private RemoteController remoteController;
    private long millisecondsTimeout = 0;


    public SteppingLauncher withAlgo(String algoName, Algo algo) {

        this.algoName = algoName;
        this.algo = algo;
        return this;
    }

    public SteppingLauncher withAlgo(Algo algo) {

        if (this.algo != null) {
            throw new SteppingException("Currently SteppingLauncher supports launching a single Algo per launch");
        }

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

    public SteppingLauncher withTimeout(long millisecondsTimeout) {
        this.millisecondsTimeout = millisecondsTimeout;
        return this;
    }
    public SteppingLauncher withContainerRegistrar(ContainerRegistrar containerRegistrar) {
        this.containerRegistrar = containerRegistrar;
        return this;
    }


    public LauncherResults launch() {
        if (subjects.isEmpty())
            throw new SteppingException("The lunch() function is used when there is a need for Stepping to wait for the accomplishment one or more Subjects before proceeding with the flow. Please attach one or more the Subjects via the stopOnSubject() API, or use the launchAndGo() function instead.");

        AlgoDecoratorLauncher algoDecoratorLauncher = new AlgoDecoratorLauncher(algo, containerRegistrar, subjects, this::launcherCallbackListener);


        remoteController = new Stepping()
                .registerAndControl(algoName, algoDecoratorLauncher)
                .go().get(algoName);


        waitTillDone();

        return new LauncherResults(subjectsStatus);
    }

    public void lunchAndGo() {
        if (!subjects.isEmpty())
            throw new SteppingException("The lunchAndGo() function is used when there is no need for Stepping to wait for the accomplishment Subjects. Please remove the Subjects attached via the stopOnSubject() API, or use the launch() function instead.");

        new Stepping()
                .registerAndControl(algoName, algo)
                .go();
    }

    private void waitTillDone() {
        synchronized (syncObj) {
            try {
                syncObj.wait(millisecondsTimeout);
                boolean allArrived = isAllArrived();
                if (!allArrived)
                    throw new SteppingLauncherTimeoutException("SteppingLauncher Timeout of " + millisecondsTimeout + " milli has exceeded");
            } catch (SteppingLauncherTimeoutException xe) {
                tryCloseAlgo();
                throw xe;
            } catch (Exception e) {
                tryCloseAlgo();
                throw new SteppingSystemException(e);
            }
        }
    }

    private void tryCloseAlgo() {
        try {
            remoteController.close();
        } catch (IOException ex) {
            logger.debug(ex.getMessage());
        }
    }

    boolean launcherCallbackListener(Data d, String s) {
        if (s.equals("POISON-PILL")) {
            releaseWait();
            return true;
        }

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
