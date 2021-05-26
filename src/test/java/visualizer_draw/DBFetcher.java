package visualizer_draw;
import com.imperva.stepping.*;

public class DBFetcher implements Step {

    private Container cntr;
    private Shouter shouter;

    @Override
    public void init(Container cntr, Shouter shouter) {
        // Container represents a place holder for general objects that must be available for multiple Steps.
        // Instead of passing these objects over and over to many functions, we use the Container too add and get the desired objects thread-safely
        this.cntr = cntr;

        // Shouter is responsible of notifying subscribers when onr or more Subjects' or Events' state change.
        this.shouter = shouter;
        //Usually Steps keeps a reference to these two objects for further usage
    }

    @Override
    public void onTickCallBack() {

        try {
            Thread.currentThread().sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Will be called periodically based on Step configuration.
        //In this case the Step is configured to request CPU time every 10 seconds see getConfig()

        /*
         * PSEUDO_CODE
         * - getDBConnectionFromPool
         * - performSQLQuery
         * - Retrieve the data
         * - Manipulate the data
         * */

        // Shouter is responsible of notifying subscribers when onr or more Subjects' or Events' state change.
        // In this case new data has arrived and manipulated by DBFetcher Step.
        //Shouter notifies and safely publishes the data to "DBDataArrived" event subscribers
        String[] manipulatedData = {"Volvo", "BMW", "Ford", "Mazda"};
        shouter.shout("DBDataArrived", manipulatedData);

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
    public void onRestate() {
        //This is a special function called just once in the lifecycle of a Step.
        //Steeping allows Steps to hook into the initialization stage, before Steps execution in order to enable Steps to
        //load the necessary state into the Step before execution. This can be useful when spawning new instances of Stepping
        //and we need to fist load some state in-memory before starting.
        //Stepping will wait for all Steps to "Re-state" before continuing the initialization process
    }



    public StepConfig getConfig() {
        //StepConfig is the Step's configuration object designed to override Steps default values.
        //In this case we enable TickCallBack event and set is the PeriodicDelay to 10 seconds
        StepConfig stepConfig = new StepConfig();
        stepConfig.setEnableTickCallback(true);
        stepConfig.setRunningPeriodicDelay(10000);//10 Seconds
        stepConfig.setMonitorEnabledForStep(true);
        stepConfig.setMonitorEmmitTimeout(13);
        return stepConfig;
    }


    @Override
    public void onKill() {
        // Cleanup before process termination
    }

    @Override
    public String getId() {
        return "DBFetcher";
    }
}