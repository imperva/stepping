package stepping;

public class GlobalAlgoStepConfig {
    private int runningInitialDelay;
    private int runningPeriodicDelay;
    private boolean runningAsDaemon;
    private IDecelerationStrategy decelerationStrategy = new DefaultLeakyBucketDecelerationStrategy();
    private boolean enableDecelerationStrategy;

    public GlobalAlgoStepConfig() {
        SteppingProperties stepProp = SteppingProperties.getInstance();
        runningInitialDelay = new Integer(stepProp.getProperty("stepping.default.algo.initialdelay"));
        runningPeriodicDelay = new Integer(stepProp.getProperty("stepping.default.algo.delay"));
        runningAsDaemon = new Boolean(stepProp.getProperty("stepping.default.algo.daemon"));
        enableDecelerationStrategy = new Boolean(stepProp.getProperty("stepping.default.algo.enable.deceleration"));
    }

    public IDecelerationStrategy getDecelerationStrategy() {
        return decelerationStrategy;
    }

    public void setDecelerationStrategy(IDecelerationStrategy iDecelerationStrategy) {
        this.decelerationStrategy = iDecelerationStrategy;
    }

    public boolean isEnableDecelerationStrategy() {
        return enableDecelerationStrategy;
    }

    public void setEnableDecelerationStrategy(boolean enableDecelerationStrategy) {
        this.enableDecelerationStrategy = enableDecelerationStrategy;
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

    public boolean isRunningAsDaemon() {
        return runningAsDaemon;
    }

    public void setRunningAsDaemon(boolean runningAsDaemon) {
        this.runningAsDaemon = runningAsDaemon;
    }
}