package Stepping;

import java.util.Calendar;
import java.util.Date;

public class DefaultDecelerationStrategy implements IDecelerationStrategy {
    private int defaultPeriodTime;
    private int minItemsInPeriod;
    private int decelerateRate;
    private int maxTimeout;
    private Date stopDate = null;
    private int totItems = 0;

    public DefaultDecelerationStrategy() {
        SteppingProperties stepProp = SteppingProperties.getInstance();
        defaultPeriodTime = new Integer(stepProp.getProperty("stepping.default.deceleration.strategy.default-period-time"));
        maxTimeout = new Integer(stepProp.getProperty("stepping.default.deceleration.strategy.max-timeout"));
        minItemsInPeriod = new Integer(stepProp.getProperty("stepping.default.deceleration.strategy.min-items-in-period"));
        decelerateRate = new Integer(stepProp.getProperty("stepping.default.deceleration.strategy.decelerate-rate"));
    }

    @Override
    public int decelerate(Date now, int itemsInQ, int currentDecelerationTimeout) {
        totItems = +itemsInQ;
        if (stopDate == null) {
            stopDate = calcStopDate();
        }

        if (stopDate.after(now)) {
            if (totItems < minItemsInPeriod && (currentDecelerationTimeout + decelerateRate) > maxTimeout) {
                stopDate = null;
                totItems = 0;
                return currentDecelerationTimeout + decelerateRate;
            } else if (totItems > minItemsInPeriod) {
                stopDate = null;
                totItems = 0;
                return 0;//* Remove any deceleration
            }
        }
        return currentDecelerationTimeout;
    }

    private Date calcStopDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND, defaultPeriodTime);
        stopDate = cal.getTime();
        return stopDate;

    }
}
