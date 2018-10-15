package stepping;

public class StepConfig {
    private int runningInitialDelay;
    private int runningPeriodicDelay;
    private boolean runningAsDaemon;
    private int numOfNodes = 0;

    public StepConfig() {
        SteppingProperties stepProp = SteppingProperties.getInstance();
        runningInitialDelay = new Integer(stepProp.getProperty("stepping.default.step.initialdelay"));
        runningPeriodicDelay = new Integer(stepProp.getProperty("stepping.default.step.delay"));
        runningAsDaemon = new Boolean(stepProp.getProperty("stepping.default.step.daemon"));
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

    public int getNumOfNodes() {
        return numOfNodes;
    }

    public void setNumOfNodes(int numOfNodes) {
        this.numOfNodes = numOfNodes;
    }
}