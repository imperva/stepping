package visualizer_draw;
import com.imperva.stepping.*;

import java.util.Arrays;
import java.util.List;

public class LogStash implements Step {

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
        // /*see comments above*/
    }

    @Override
    public void onTickCallBack() {
        //Will be called periodically based on Step configuration.
        //In this case the Step is *NOT* configured to request CPU at all so this function won't be called
    }

    @Override
    public void listSubjectsToFollow(Follower follower){
        follower.follow("MergerDone");
        //This function is called on Stepping initialization for each registered Subject.
        //This is the way to notify Stepping which events (Subjects) we are interested in.
        //In this case we need to subscribe to a subset of Subjects so it makes sense to use the new API:


    }



    /***** OLD API
     @Override
     public boolean followsSubject(String subjectType) {
     //This function is called on Stepping initialization for each registered Subject.
     //This is the way to notify Stepping which events (Subjects) we are interested in.
     //Returning true means we are interested in the event
     if ("DBDataArrived".equals(subjectType) ||
     "KafkaDataArrived".equals(subjectType)) {
     return true;
     }
     return false;
     }*/

    @Override
    public void onSubjectUpdate(Data data, String subjectType) {
        try {
            Thread.currentThread().sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /* PSEUDO CODE
         * - Merge Kafka and DB data
         * - Write data back to Kafka
         * */

        // NOTE: In this case no one is subscribed to "MergerDone" event (Subject) so no Step will be notified
        String[] mergedData = {"Volvo", "BMW", "Ford", "Mazda"};
        //shouter.shout("MergerDone", mergedData);
        System.out.println("Log Stash done!. ");
    }

    @Override
    public void onRestate() { /*see comments above*/  }

    public StepConfig getConfig() {
        StepConfig stepConfig = new StepConfig();
        stepConfig.setEnableTickCallback(false);
        stepConfig.setMonitorEnabledForStep(true);
        stepConfig.setMonitorEmmitTimeout(11);
        return stepConfig;
    }

    @Override
    public void onKill() { /*see comments above*/  }

    @Override
    public String getId() {
        return "LogStash";
    }
}