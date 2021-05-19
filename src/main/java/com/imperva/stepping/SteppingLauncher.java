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
    private HashMap<String, WithShoutConfig> shouts = new HashMap<>();
    private volatile boolean allSubjectsDetected;


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
        SystemStepExternalConfig systemStepExternalConfig = new SystemStepExternalConfig(step, stepConfig);
        containerRegistrar.add(systemStepExternalConfig);
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

    public SteppingLauncher withShout(String subject, Data data) {
        withShout(subject, data, 1);
        return this;
    }

    public SteppingLauncher withShout(String subject, Data data, int numOfRecurrences) {
        shouts.put(subject, new WithShoutConfig(data, numOfRecurrences));
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


        tryShout();

        waitTillDone();

        return new LauncherResults(subjectsStatus);
    }

    private boolean launcherCallbackListener(Data d, String s) {
        synchronized (syncObj) {
            boolean isPoisonPill = s.equals("LAUNCHER-POISON-PILL");
            if (!isPoisonPill)
                subjectsStatus.put(s, d);

            allSubjectsDetected = checkAllSubjectsDetected();
            if (allSubjectsDetected || isPoisonPill) {
                syncObj.notifyAll();
                return true;
            }
            return false;
        }
    }

    public void lunchAndGo() {
        if (!subjects.isEmpty())
            throw new SteppingException("The lunchAndGo() function is used when there is no need for Stepping to wait for the accomplishment Subjects. Please remove the Subjects attached via the stopOnSubject() API, or use the launch() function instead.");

        new Stepping()
                .registerAndControl(algoName, algo)
                .go();

        tryShout();
    }


    private void tryShout() {
        for (Map.Entry<String, WithShoutConfig> entry : shouts.entrySet()) {
            int numOfRecurrencesToCheck = entry.getValue().numOfRecurrences - 1;
            for (int i = 0 ; i <= numOfRecurrencesToCheck; i++){
                remoteController.getShouter().shout(entry.getKey(), entry.getValue().data);
            }
        }
    }

    private void waitTillDone() {
        synchronized (syncObj) {
            try {
                if (allSubjectsDetected)
                    return;

                syncObj.wait(millisecondsTimeout);//todo: maybe surround by while as described by documentation
                if (!allSubjectsDetected) {
                    throw new SteppingLauncherTimeoutException("SteppingLauncher Timeout of " + millisecondsTimeout + " milli has exceeded");
                }
            } catch (SteppingLauncherTimeoutException xe) {
                throw xe;
            } catch (Exception e) {
                throw new SteppingSystemException(e);
            } finally {
                tryCloseAlgo();
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

    private boolean checkAllSubjectsDetected() {
        boolean allArrived = true;
        for (Map.Entry<String, Data> entry : subjectsStatus.entrySet()) {
            if (entry.getValue() == null) {
                allArrived = false;
                break;
            }
        }
        return allArrived;
    }


    private class WithShoutConfig {
        private Data data;
        private int numOfRecurrences;

        WithShoutConfig(Data data, int numOfRecurrences) {
            this.data = data;
            this.numOfRecurrences = numOfRecurrences;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public int getNumOfRecurrences() {
            return numOfRecurrences;
        }

        public void setNumOfRecurrences(int numOfRecurrences) {
            this.numOfRecurrences = numOfRecurrences;
        }
    }
}
