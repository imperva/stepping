package visualizer_draw;
import com.imperva.stepping.*;

import java.util.concurrent.TimeUnit;

public class KafkaFetcher implements Step {
    private Container cntr;
    private Shouter shouter;

    private int tickCounter = 0;

    @Override
    public void init(Container cntr, Shouter shouter) {
        // Container represents a place holder for general objects that must be available for multiple Steps.
        // Instead of passing these objects over and over to many functions, we use the Container too add and get the desired objects thread-safely
        this.cntr = cntr;

        // Shouter is responsible of notifying subscribers when onr or more Subjects' or Events' state change.
        this.shouter = shouter;
        //Usually Steps keeps a reference to these two objects for further usage
        // /*see comments above*/
    }

    @Override
    public void onTickCallBack() {
        tickCounter++;

        try {
            Thread.currentThread().sleep(1800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Will be called periodically based on Step configuration.
        //In this case the Step is configured to request CPU time every 1 millisecond see getConfig()

        /*
         * PSEUDO_CODE
         * - listenOnKafkaTopic
         * - On data arrival perform manipulation
         * */

        //see comments above
        String[] manipulatedData = {"Volvo", "BMW", "Ford", "Mazda"};
        shouter.shout("KafkaDataArrived", manipulatedData);
    }



    @Override
    public boolean followsSubject(String subjectType) {
        //This Step has no need to listen to other Steps' events, its an entry-point Step it makes use of onTickCallBack
        return false;
    }

    @Override
    public void onSubjectUpdate(Data data, String subjectType) {
        // Not Applicable
    }

    @Override
    public void onRestate() { /*see comments above*/  }



    public StepConfig getConfig() {
        //StepConfig is the Step's configuration object designed to override Steps default values.
        //In this case we enable TickCallBack event and set is the PeriodicDelay to 1 millisecond
        StepConfig stepConfig = new StepConfig();
        stepConfig.setEnableTickCallback(true);
        stepConfig.setRunningPeriodicDelayUnit(TimeUnit.SECONDS);
        stepConfig.setRunningPeriodicDelay(10);
        stepConfig.setMonitorEnabledForStep(false);
        stepConfig.setMonitorEmmitTimeout(12);
        return stepConfig;
    }


    @Override
    public void onKill() { /*see comments above*/  }

    @Override
    public String getId() {
        return "KafkaFetcher";
    }
}