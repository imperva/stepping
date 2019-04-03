# Stepping 
Stepping is a framework designed to ease the implementation of data processing solutions.
In use cases where we need to implement data or data-streaming algorithms or any other processing on data, we need to 
first handle many different infrastructure issues.

For example, we need to decide how to split the data processing logic into different steps, think about our threading policy, 
how to handle communication between the different steps, error handling etc.
One of the most important subjects is the Threading Policy of our solution. For example, we need to think how many threads 
to open, have the option to distribute the processing of data to multiple 'executors' in parallel, have a thread-safe 
communication layer between the threads etc.
On top of that we also care a lot about the performance of our solution, we want to make sure that the latency added by 
these infrastructures is minimal as possible. 

Stepping aims to handle many of these aspects so developers can spend their time on the business logic instead of 
solving these infrastructure and data flow issues issues over and over again. 

# Benefits
The goal of this framework is to solve infrastructure issues encountered when developing data or data-streaming processing solution.
Stepping is an event-driven, multithreaded, thread-safe (lockless) framework that handles the following:

- Handling concurrency and thread safety
- Expose an easy way to split the entire data-processing logic into small units (steps) making sure that all the units are always busy and efficient  
- Communication between the different parts (steps) of the solution
- Enable consumers to split the processing data to be processed in parallel by different units (steps)
- Expose an extremely easy API to implement on top of it data-streaming processes logic 
- Enable customers to implement custom error handling policies

# Dependencies
- slf4j-simple
- slf4j-api

# Basics
Stepping has three main players: Algos, Steps and Subjects.

### Algos
Algos are containers of Steps, each Algo contains a Step or more (there are no limits on the number of steps an Algo can 
contain apart machine's limits like RAM, CPU etc).
An Algo is responsible of initiating the steps, handling errors and exposing Stepping's external API to consumers.

### Steps
Steps are contained and initialized by their Algo and contains & execute the data-streaming logic (consumers' business logic).
Each Step register itself to Subjects (events) it is interested and the framework makes sure that whenever a Subject is 
triggered the registered Steps will get notified.
Once a Subjects is triggered the relevant Steps get a chance to perform their data processing. 
When data processing stage is done, Steps can notify subscribers (other steps subscribed to their event) and send them 
the result of the processing stage so they can execute further logic on the data.
Stepping usually runs in the background as service/daemon and executes internally at least two Steps, one acts as the 
entry-point which reads data from a data source and another Step that acts as an ending-point usually writes back the 
result to a streaming platform, a file or into a DB.

Steps can't communicate directly  with other Steps just by calling their function. The communication is event-driven and 
Stepping makes sure that communicating Steps don't interfere with each other and release the Steps fast as possible.
  
### Subjects
Subjects are entities that represents events that Steps can subscribe to based on their business logic needs.
Once a Step register himself to a Subject, Stepping will make sure to notify it on each update. 

### onTickCallBack
TickCallBack is not a player in Stepping but is a fundamental functionality.
Often, when working on data we need to perform some work (computation, I/O tasks) periodically, based on a specific timeout.   
In these cases we can't relay only on the CPU time we get when an event we are interested in was triggered, we need a way 
to get some CPU time periodically.
By configuring your Step to enable TickCallBack Stepping will take care of the rest and periodically assign to your Step 
CPU time based on the timeout specified in the configuration. 

Sometimes we need to perform some periodic, cross Steps processing, in this case we can enable  TickCallBack on the Algo itself.

# How it Works
First thing to do is to create an Algo which acts as a container and initializer of Steps. Than we will Create three Steps:
- Step1: Fetches periodically data form a DataBase and publish a "DBDataArrived" Subject
- Step2: Fetches streams of data from a kafka cluster and publish a "KafkaDataArrived" Subject
- Step3: Register himself to "DBDataArrived" and "KafkaDataArrived", performs a simple merge and writes the data to a new Kafka topic

### Hands-On
Create 3 new Classes that implements the Step Interface and leave the functions empty. Let's call the classes them as 
follows: "DBFetcher", "KafkaFetcher", "Merger". 
Now create your first Algo, by creating a new Class "KafkaDBMergerAlgo" that implements the Algo Interface, and register 
the Steps that you just created.
We already know which event each Step will trigger, so we can also register the Subjects into our "KafkaDBMergerAlgo":

```java
public class KafkaDBMergerAlgo implements Algo {

    public KafkaDBMergerAlgo() {    }

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
        // and enable the retrieval of these objects via the Container object visible to all the Steps
        ContainerRegistrar containerRegistrar = new ContainerRegistrar();

        //* init subjects
        ISubject dbDataArrivedSubject = new Subject("DBDataArrived");
        containerRegistrar.add(subject.getType(), dbDataArrivedSubject);
        
        ISubject kafkaDataArrivedSubject = new Subject("KafkaDataArrived");
        containerRegistrar.add(subject.getType(), kafkaDataArrivedSubject);
        
        
        //* init Steps
        containerRegistrar.add("DBFetcher", new DBFetcher());
        containerRegistrar.add("KafkaFetcher", new KafkaFetcher());
        return containerRegistrar;
    }



    @Override
    public void close() {
       // Cleanup before process termination
    }
}
```

This is it! now we are ready to deal with our business logic of each of our registered Steps.
Let's start with the "DBFetcher" Step. This Step polls data from a DB every second, makes some data manipulation and 
notifies subscribers of "DBDataArrived" that DB data is ready.
```java
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
        return stepConfig;
    }


    @Override
    public void onKill() {
        // Cleanup before process termination
    }
}

```
Once "DBDataArrived" was triggered, the "DBFetcher" Step is free to go to fetch the next chunk of data, while other Steps 
take care of this chunk.
  
 
The "KafkaFetcher" Step is similar to the "DBFetcher" Step. This Step is a Kafka Consumer that constantly receives streams 
of data, makes some data manipulation and notifies subscribers of "KafkaDataArrived" that Kafka data is ready.
```java
public class KafkaFetcher implements Step {

    @Override
    public void init(Container cntr, Shouter shouter) { /*see comments above*/  }

    @Override
    public void onTickCallBack() {
       //Will be called periodically based on Step configuration. 
       //In this case the Step is configured to request CPU time every 1 millisecond see getConfig()
       
       /*
       * PSEUDO_CODE
       * - listenOnKafkaTopic
       * - On data arrival perform manipulation
       * */
       
      //see comments above
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
        stepConfig.setRunningPeriodicDelay(1);//1 millisecond
        return stepConfig;
    }


    @Override
    public void onKill() { /*see comments above*/  }
}

```
Just as with "DBFetcher", once "KafkaDataArrived" event is triggered, the "KafkaFetcher" Step is ready to receive more streams of data, while other Steps take further care of the current stream.

The last Step to implement is the "Merger" which is in charge to merge these these two source of data and write them back into a Kafka topic.
```java
public class Merger implements Step {

    private Container cntr;
    private Shouter shouter;

    @Override
    public void init(Container cntr, Shouter shouter) { /*see comments above*/  }

    @Override
    public void onTickCallBack() {
       //Will be called periodically based on Step configuration. 
       //In this case the Step is *NOT* configured to request CPU at all so this function won't be called
    }

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
    }

    @Override
    public void onSubjectUpdate(Data data, String subjectType) {
        //This function is called by Stepping whenever a Subject that the Step registered to was triggered in this case 
        // "DBDataArrived" and "KafkaDataArrived"
        if(subjectType.equals("DBDataArrived")){
            //do that
        } else if(subjectType.equals("KafkaDataArrived")){
            //do that 
        }
        
        /* PSEUDO CODE
         * - Merge Kafka and DB data
         * - Write data back to Kafka
         * */
        
        // NOTE: In this case no one is subscribed to "MergerDone" event (Subject) so no Step will be notified 
        shouter.shout("MergerDone", mergedData);
    }

    @Override
    public void onRestate() { /*see comments above*/  }

    public StepConfig getConfig() {
        StepConfig stepConfig = new StepConfig();
        stepConfig.setEnableTickCallback(false);
        return stepConfig;
    }

    @Override
    public void onKill() { /*see comments above*/  }
}

```
As we can see the "Merger" Step is the first Step in this exercise that implements the followsSubject() and onSubjectUpdate() functions.
"DBFetcher" and "KafkaFetcher" are two entry-point Steps, they use "TickCallBack" function to get CPU time to fetch their 
data but their work does not depend on external events of other Steps therefore they do not register to any event.
In this example "Merger" is the first Step that depends on events triggered be other Steps, "DBFetcher" and "KafkaFetcher".

Now you are probably curios to see how we run it. Actually it is very simple:

```java
public class Main {

    public static void main(String[] args)  {
        new Stepping().register(new KafkaDBMergerAlgo()).go();
    }
}
```
Stepping makes use of non-daemon threads so the application is kept alive till closed/shutdown externally.


# Concurrency Model

### Steps and Threads
Stepping is multithreaded in sense that each Step runs in its own thread to maximize efficiency of the Steps. 
While Step B is busy executing its logic on the data Step A can start executing its logic on a different chunk of data.
Usually there are always threads that performs I/O so the number of Steps can be grater than the number of cores of the machine.

Splitting the logic into different Steps is important not only to gain threads efficiency but also for separation of concerns, 
so creating more Steps then the number of cores in the machine is still acceptable unless you create dozens of steps on a 4 core machine. 
In this case you might discover that your efficiency actually decreases. 

### Concurrency Policy
Although each Step works in a dedicated thread, inside the Steps Stepping makes sure that only one thread executes a Step's function.  
For example while onSubjectUpdate() is executed to to an event change, onTickCallBack() will not be executed, onTickCallBack() 
will wait till current execution is done before start executing.

This Thread Policy allows consumers of Stepping  (Steps implementors) to use local variables inside their Steps as the 
variables are always accessed by the *same* single thread thus avoiding visibility issues between threads.

Stepping is lockless, meaning that threads' flow is not controlled by locks which might hurt the concurrency of the program.

NOTE: When triggering events via the Shouter objects the consumers must make sure to not read or write data into an object 
already published. Doing that might create visibility and condition race issues.   

# Configuration
Stepping contains a single configuration file at this location:
/stepping/src/main/resources/stepping.properties


# Getting Help
If you have questions about the library, please be sure to check out the API documentation. 
If you still have questions, reach out me via mail gabi.beyo@imperva.com.
  
