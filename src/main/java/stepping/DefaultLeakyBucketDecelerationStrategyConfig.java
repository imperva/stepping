//package stepping;
//
//public class DefaultLeakyBucketDecelerationStrategyConfig {
//    private int maxDecelerationDelay;
//    private int defaultInspectionPeriodTime;
//    private int maxItemsInPeriod;
//    private int decelerateRate;
//    private int accelerationRate;
//
//
//    public DefaultLeakyBucketDecelerationStrategyConfig() {
//        SteppingProperties stepProp = SteppingProperties.getInstance();
//        maxDecelerationDelay = new Integer(stepProp.getProperty("stepping.default.deceleration.strategy.max-deceleration-delay"));
//        defaultInspectionPeriodTime = new Integer(stepProp.getProperty("stepping.default.deceleration.strategy.default-inspection-period-time"));
//        maxItemsInPeriod = new Integer(stepProp.getProperty("stepping.default.deceleration.strategy.max-items-in-period"));
//        decelerateRate = new Integer(stepProp.getProperty("stepping.default.deceleration.strategy.deceleration-rate"));
//        accelerationRate = new Integer(stepProp.getProperty("stepping.default.deceleration.strategy.acceleration-rate"));
//
//
//    }
//
//    public int getDefaultInspectionPeriodTime() {
//        return defaultInspectionPeriodTime;
//    }
//
//    public void setDefaultInspectionPeriodTime(int defaultInspectionPeriodTime) {
//        this.defaultInspectionPeriodTime = defaultInspectionPeriodTime;
//    }
//
//    public int getMaxItemsInPeriod() {
//        return maxItemsInPeriod;
//    }
//
//    public void setMaxItemsInPeriod(int maxItemsInPeriod) {
//        this.maxItemsInPeriod = maxItemsInPeriod;
//    }
//
//    public int getDecelerateRate() {
//        return decelerateRate;
//    }
//
//    public void setDecelerateRate(int decelerateRate) {
//        this.decelerateRate = decelerateRate;
//    }
//
//    public int getMaxDecelerationDelay() {
//        return maxDecelerationDelay;
//    }
//
//    public void setMaxDecelerationDelay(int maxDecelerationDelay) {
//        this.maxDecelerationDelay = maxDecelerationDelay;
//    }
//
//    public int getAccelerationRate() {
//        return accelerationRate;
//    }
//
//    public void setAccelerationRate(int accelerationRate) {
//        this.accelerationRate = accelerationRate;
//    }
//}
