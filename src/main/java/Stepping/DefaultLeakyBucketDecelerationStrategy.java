package Stepping;

import java.util.Calendar;
import java.util.Date;

public class DefaultLeakyBucketDecelerationStrategy implements IDecelerationStrategy {
    private DefaultLeakyBucketDecelerationStrategyConfig conf;

    private Date stopDate = null;
    private int totItems = 0;

    public DefaultLeakyBucketDecelerationStrategy() {this(new DefaultLeakyBucketDecelerationStrategyConfig());}

    public DefaultLeakyBucketDecelerationStrategy(DefaultLeakyBucketDecelerationStrategyConfig conf) {
        this.conf = conf;
    }

    @Override
    public int decelerate(Date now, int itemsInQ, int currentDecelerationTimeout) {
        totItems += itemsInQ;
        if (stopDate == null) {
            stopDate = calcStopDate();
        }

        if (stopDate.before(now)) {
            int increasedDeceleration = currentDecelerationTimeout + conf.getDecelerateRate();
            int decreasedDeceleration = currentDecelerationTimeout - conf.getAccelerationRate();
            if (totItems < conf.getMaxItemsInPeriod() && increasedDeceleration < conf.getMaxDecelerationDelay()) {

                System.out.println("DefaultLeakyBucketDecelerationStrategy: " + increasedDeceleration);
                return increasedDeceleration;
            } else if (totItems >= conf.getMaxItemsInPeriod() && decreasedDeceleration > 0) {

                System.out.println("DefaultLeakyBucketDecelerationStrategy: " + decreasedDeceleration);
                return decreasedDeceleration;//* Remove any deceleration
            }
            stopDate = null;
            totItems = 0;
        }
        System.out.println("DefaultLeakyBucketDecelerationStrategy: " + currentDecelerationTimeout);
        return currentDecelerationTimeout;
    }

    private Date calcStopDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND, conf.getDefaultInspectionPeriodTime());
        stopDate = cal.getTime();
        return stopDate;

    }
}
