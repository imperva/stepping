package Stepping;

public class StepConfig {
   private IDecelerationStrategy decelerationStrategy = new DefaultDecelerationStrategy();
   private boolean enableDecelerationStrategy = false;


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
}
