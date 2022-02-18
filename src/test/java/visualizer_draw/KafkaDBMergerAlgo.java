package visualizer_draw;

import com.imperva.stepping.Algo;
import com.imperva.stepping.AlgoConfig;
import com.imperva.stepping.ContainerRegistrar;

public class KafkaDBMergerAlgo implements Algo {

    private ContainerRegistrar containerRegistrar;
    public KafkaDBMergerAlgo() {
        containerRegistrar = new ContainerRegistrar();

        containerRegistrar.add(new DBFetcher());
//        containerRegistrar.add(new KafkaFetcher());
        containerRegistrar.add(new Merger());
        containerRegistrar.add(new LogStash());
    }

    @Override
    public void onTickCallBack(){
        //Will be called periodically based on the Algo configuration.
        //In this case the Algo is configured to request CPU time every second see getConfig()
    }

    @Override
    public AlgoConfig getConfig() {
        //AlgoConfig is the Algo's configuration object designed to override Algos default values.
        //In this case we enable TickCallBack event and set is the PeriodicDelay to 1 second
        AlgoConfig algoConfig = new AlgoConfig();
        algoConfig.setEnableTickCallback(true);
        algoConfig.setRunningPeriodicDelay(1000);//1 sec
        algoConfig.setInitMonitorCollector(true);
        return algoConfig;
    }

    @Override
    public void init() {
        //Stepping gives a change to Algos to perform some general logic before starting the Steps

    }

    @Override
    public ContainerRegistrar containerRegistration() {
        //This function is called by Stepping to enable Algos register Steps and Subjects.
        //ContainerRegistrar can be used to register any arbitrary/cutom object. By doing so Stepping will keep it in-memory
        //and enable the retrieval of these objects via the Container object visible to all the Steps


        /**** init subjects - NOTE: As we now can use (3.6.x) the new Follower.follow() API to register
         * Steps to Subjects, there is no need explicitly create and register Subjects.
         *
         ISubject dbDataArrivedSubject = new Subject("DBDataArrived");
         containerRegistrar.add(subject.getType(), dbDataArrivedSubject);

         ISubject kafkaDataArrivedSubject = new Subject("KafkaDataArrived");
         containerRegistrar.add(subject.getType(), kafkaDataArrivedSubject);
         */

        /* NOTE: Since version 3.6.x, ContainerRegistrar supports two methods for registration: one for registering an IIdentity object and one for all other objects types. IIdentity objects must have an ID by overriding the IIdentity interface, otherwise,
        the registration will fail. The other method allows to register any other type object, in this case you need to use the method that receives the object and a unique identifier. If you try to call this method with an
        IIdentity object or with an invalid ID, the registration will fail.
        */


        return containerRegistrar;
    }



    @Override
    public void close() {
        // Cleanup before process termination
    }
}