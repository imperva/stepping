package Stepping;

public class StepConfig {
   private IDecelerationStrategy  iDecelerationStrategy = new DefaultDecelerationStrategy();
   private boolean enableDecelerationStrategy = false;


    public IDecelerationStrategy getDecelerationStrategy() {
        return iDecelerationStrategy;
    }

    public void setDecelerationStrategy(IDecelerationStrategy iDecelerationStrategy) {
        this.iDecelerationStrategy = iDecelerationStrategy;
    }

    public boolean isEnableDecelerationStrategy() {
        return enableDecelerationStrategy;
    }

    public void setEnableDecelerationStrategy(boolean enableDecelerationStrategy) {
        this.enableDecelerationStrategy = enableDecelerationStrategy;
    }
}
