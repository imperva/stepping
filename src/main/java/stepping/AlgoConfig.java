package stepping;

public class AlgoConfig {
    private int runningInitialDelay;
    private int runningPeriodicDelay;
    private boolean enableTickCallback;

    public AlgoConfig() {
        SteppingProperties stepProp = SteppingProperties.getInstance();
        runningInitialDelay = new Integer(stepProp.getProperty("stepping.default.algo.initialdelay"));
        runningPeriodicDelay = new Integer(stepProp.getProperty("stepping.default.algo.delay"));
        enableTickCallback = new Boolean(stepProp.getProperty("stepping.default.algo.enable.tickcallback"));
    }

    public int getRunningInitialDelay() {
        return runningInitialDelay;
    }

    public void setRunningInitialDelay(int runningInitialDelay) {
        this.runningInitialDelay = runningInitialDelay;
    }

    public int getRunningPeriodicDelay() {
        return runningPeriodicDelay;
    }

    public void setRunningPeriodicDelay(int runningPeriodicDelay) {
        this.runningPeriodicDelay = runningPeriodicDelay;
    }

    public boolean isEnableTickCallback() {
        return enableTickCallback;
    }

    public void setEnableTickCallback(boolean enableTickCallback) {
        this.enableTickCallback = enableTickCallback;
    }
}