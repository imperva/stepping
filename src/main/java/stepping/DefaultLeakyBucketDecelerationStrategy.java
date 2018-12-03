//package stepping;
//
//import java.util.Date;
//
//public class DefaultLeakyBucketDecelerationStrategy implements IDecelerationStrategy {
//    private DefaultLeakyBucketDecelerationStrategyConfig conf;
//
//    private Long stopDate = null;
//    private int totItems = 0;
//
//    public DefaultLeakyBucketDecelerationStrategy() {this(new DefaultLeakyBucketDecelerationStrategyConfig());}
//
//    public DefaultLeakyBucketDecelerationStrategy(DefaultLeakyBucketDecelerationStrategyConfig conf) {
//        this.conf = conf;
//    }
//
//    @Override
//    public int decelerate(Date now, int itemsInQ, int currentDecelerationTimeout) {
//        totItems += itemsInQ;
//        if (stopDate == null) {
//            stopDate = calcStopDate(now);
//        }
//
//        if (stopDate < now.getTime()) {
//            int increasedDeceleration = currentDecelerationTimeout + conf.getDecelerateRate();
//            int decreasedDeceleration = currentDecelerationTimeout - conf.getAccelerationRate();
//            if (totItems < conf.getMaxItemsInPeriod() && currentDecelerationTimeout < conf.getMaxDecelerationDelay()) {
//
//
//                return increasedDeceleration > conf.getMaxDecelerationDelay() ? conf.getMaxDecelerationDelay() : increasedDeceleration;
//            } else if (totItems >= conf.getMaxItemsInPeriod() && currentDecelerationTimeout > 0) {
//
//
//                return decreasedDeceleration < 0 ? 0 : decreasedDeceleration;//* Remove any deceleration
//            }
//            stopDate = null;
//            totItems = 0;
//        }
//
//        return currentDecelerationTimeout;
//    }
//
//    private Long calcStopDate(Date now) {
//        return now.getTime() + conf.getDefaultInspectionPeriodTime();
////        Calendar cal = Calendar.getInstance();
////        cal.add(Calendar.MILLISECOND, conf.getDefaultInspectionPeriodTime());
////        stopDate = cal.getTime();
////        return stopDate;
//
//    }
//}
