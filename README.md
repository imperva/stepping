[![Maven Central](https://img.shields.io/maven-central/v/com.imperva.stepping/stepping.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.imperva.ddc%22%20AND%20a:%22stepping%22)

[![Build Status](https://www.travis-ci.org/imperva/stepping.svg?branch=master)](https://www.travis-ci.org/imperva/stepping?branch=master)


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
solving these infrastructure and data flow issues, over and over again. 

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
- perf-sampler

# How To Use It - First Steps
Stepping is a Maven project (binaries are deployed in Maven Central) so you can import the projects manually or via Maven by adding the following Dependencies to your project's POM file:

~~~
<dependency>
  <groupId>com.imperva.stepping</groupId>
  <artifactId>stepping</artifactId>
  <version>3.6.3</version>
</dependency>
~~~

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
Stepping makes sure that communicating Steps don't interfere with each other and release the Steps fast as possible,  
behind the scenes, Stepping makes us of in-memory queues to handle the incoming messages.

#### Multiple Algos on a Single Process
In order to allow maximum flexibility and physical resource utilization, Stepping supports initialization of multiple Algos
within a single process. 

This feature is useful in cases where you have enough resources to handle multiple Algos in the same process.
Because Algos can't communicate with each other directly, you can always rest assure that when the Algos will be deployed separately,
it can be done smoothly without fearing of un-expected behaviour:

```java

 new Stepping()
    .register(algo1)
    .register(algo2)
    .register(algo3).go();
```

### Set Bound Queue Capacity
Since version 3.6.x, Stepping enables clients to bound each Step's internal queue to a specific amount of messages, in case
a Step hits this predefined size, the event (Subject) 'caller' will hang till the 'Callee' (destination Step) deque some messages.

This can be ry helpful to prevent memory to crash the application. For example imagine your application suffers of random 
peaks of traffic and your Step struggles to process all the amount of data arrived. 

In this case you can protect your Step by setting a bound capacity limit for its queue. By doing so the application might move a bit slower as the 'Caller' will hang
till the 'Callee' process some messages, but it will prevent the application from undesired crashes.

To set the BoundQueue capacity you just need specify the maximum number of messages in the desired Steps Config:
```java
public class MyStep implements Step {

    public StepConfig getConfig() {
        StepConfig stepConfig = new StepConfig();
        stepConfig.setBoundQueueCapacity(10000);//Maximum 10K messages
        return stepConfig;
    }
}
```

 

### Subjects
Subjects are entities that represents events that Steps can subscribe to based on their business logic needs.
Once a Step register himself to a Subject, Stepping will make sure to notify it on each update. 

#### followsSubject vs Follower
Since version 3.6.x there are two ways to subscribe to a subject. The good old boolean followsSubject(String subjectType) 
and the new void listSubjectsToFollow(Follower follower).

followsSubject() gets a subject as input and expects to get a true/false for each Subject registered in Algo's containerRegistration() 
(See more about that in the sample below). True means that the Steps wants to register to the subject and false the opposite.

This API is good when we want to subscribe to all or ignore all the Subjects, but it requires tedious if/else statements 
and an explicitly registration of the Subject in Algo's containerRegistration().

In many cases you just want to subscribe to just a subset of the events, in this case you can use the new API: 
void listSubjectsToFollow(Follower follower). 

With this function you can append the names of the subjects you are interested in 
and Stepping will make sure to both, register your Step and even create the Subject for you, so you don't need anymore to 
explicitly register it in Algo's containerRegistration():

```java

    @Override
    public void listSubjectsToFollow(Follower follower){
        follower.follow("subjectA").follow("subjectB").follow("subjectC");
    }
    
```

listSubjectsToFollow(), if implemented will be called and used by default while followsSubject() will be called only in
cases where listSubjectsToFollow() was left empty.


### onTickCallBack
TickCallBack is not a player in Stepping but is a fundamental functionality.

Often, when working on data we need to perform some work (computation, I/O tasks) periodically, based on a specific timeout.   
In these cases we can't relay only on the CPU time we get when an event we are interested in was triggered, we need a way 
to get some CPU time periodically.

By configuring your Step to enable TickCallBack Stepping will take care of the rest and periodically assign to your Step 
CPU time based on the timeout specified in the configuration. 

Sometimes we need to perform some periodic, cross Steps processing, in this case we can enable  TickCallBack on the Algo itself.

TickCallback can be enabled via Step or Algo configuration:
```java
public class MyStep implements Step {

    public StepConfig getConfig() {
        StepConfig stepConfig = new StepConfig();
        stepConfig.setEnableTickCallback(true);
        stepConfig.setRunningPeriodicDelay(1000);//The TickCallBack function will be called each 1 sec
        return stepConfig;
    }
}
```
#### Adjust onTickCallBack at runtime
Since version 3.6.x it is possible to adjust or cancel TickCallBack timeout at runtime:

```java
  RunningScheduled running = ((ContainerService)cntr).getTickCallbackRunning(getId());
  running.changeDelay(20, TimeUnit.SECONDS);//Adjust the delay
  //...OR...
  running.stop();//Completely stop TickCallBack
```   
The Container object injected into the Step at the init() phase can be casted to a ContainerService object which expends
Container's capabilities. 

One of this new capabilities is the easy way og get the Step's TickCallBack representation (RunningScheduled)  
just be providing the Step's ID. Once the RunningScheduled is available you can adjust the RunningScheduled or even stop 
it completely.

This feature can be very usefull in cases you don't have a specific timeout delay for your TickCallBack, instead you want
to change the timeout period based on some internal logic.


# How it Works
Let's say we need to implement a simple application that fetches data from two different sources, merged the data based on
a business logic and writes the data to 3rd kafka destination.

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
        //and enable the retrieval of these objects via the Container object visible to all the Steps
        ContainerRegistrar containerRegistrar = new ContainerRegistrar();

        /**** init subjects - NOTE: As we now can use (3.6.x) the new Follower.follow() API to register
        * Steps to Subjects, there is no need explicitly create and register Subjects.
        * 
        ISubject dbDataArrivedSubject = new Subject("DBDataArrived");
        containerRegistrar.add(subject.getType(), dbDataArrivedSubject);
        
        ISubject kafkaDataArrivedSubject = new Subject("KafkaDataArrived");
        containerRegistrar.add(subject.getType(), kafkaDataArrivedSubject);
        */
        
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

NOTE: Since version 3.6.x, ContainerRegistrar supports two methods for registration: one for registering an IIdentity object and one for all other objects types. IIdentity objects must have an ID by overriding the IIdentity interface, otherwise, 
the registration will fail. The other method allows to register any other type object, in this case you need to use the method that receives the object and a unique identifier. If you try to call this method with an
IIdentity object or with an invalid ID, the registration will fail.

Steps are by default IIdentity and have a default getId() implementation.

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
    public void listSubjectsToFollow(Follower follower){
        //This function is called on Stepping initialization for each registered Subject.
        //This is the way to notify Stepping which events (Subjects) we are interested in.
        //In this case we need to subscribe to a subset of Subjects so it makes sense to use the new API:
        follower.follow("DBDataArrived").follow("KafkaDataArrived");
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
As we can see the "Merger" Step is the first Step in this exercise that implements the followsSubject() and onSubjectUpdate() 
functions.

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


# Advanced Topics

### Exception Handling
Steeping provides its internal error handling for un-handled exception. 

The builtin implementation will try to delegate the exception handling to client's custom ExceptionHandler if provided 
(more about this in the next paragraph), otherwise it will try to gracefully shutdown all the steps, give a change to 
all Steps to cleanup and die gracefully.

### Custom ExceptionHandling
Steeping enables consumers to provide their own Exception logic and notify the framework whether it was able to handle the
exception, in this case the builtin Exception handling is suppressed, otherwise Stepping will trigger the default behaviour.

To set your customeException Handler you just need to supply an IExceptionHandler implementation to your AlgoConfig:
```java
    public AlgoConfig getConfig() {
        AlgoConfig algoConfig = new AlgoConfig();
        algoConfig.setCustomExceptionHandler(new IExceptionHandler() {
            @Override
            public boolean handle(Exception e) {
                return false;
            }
        });
        return algoConfig;
    }
```

### Kill Process
In case a single process hosts multiple Algos, Stepping expose a way to kill the entire process in case of exception, including 
working Steps that are not the cause of the failure. This way you can rest assure that if needed the entire process will
shutdown and not hang-up. 

To support that a new SteppingSystemCriticalException has been introduced in version 3.6.x.

By throwing this exception Stepping you instruct Stepping to consider the exception as Critical and thus enable Stepping 
to kill the entire process.

NOTE: Currently this feature works for by sending a 'kill <pid>' command to the process and thus triggers internal ShutdownHook 
methods to gracefully close the process. In this version SteppingSystemCriticalException feature is not available for Stepping 
running on Windows.

### Distribution Strategy 
When "Shout" is triggered, internally Stepping detects the DistributionPolicy attached to the 'callee' Step (the destination Step),
and delegates the handling to its the distribution logic. Stepping delivers two basic builtin Distribution Strategy: 

- All2AllDistributionStrategy: This policy is the default and the simplest one. It is designed to send the same data to each 
Step that is registered to the specific 'Subject'

- EvenDistributionStrategy - This policy is the default behaviour used for Duplicated Nodes (more about this in the next paragraph).
In this case each duplicated node will get an even chunk of data.

Stepping enables consumers to specify their own behaviour be supply a custom Distribution Policy. 
The Distribution Policy must implements the IDistributionStrategy interface, and configure the Step's configuration:


```java
public class MyStep implements Step {

    public StepConfig getConfig() {
        stepConfig.setDistributionStrategy(new MyCustomDistributionPolicy());;//MyCustomDistributionPolicy must implement IDistributionStrategy
        return stepConfig;
    }
}
```

### Steps Identity
Since version 3.6.x each Step implements interface Identity meant to give each Step a unique friendly name. This interface 
has a default implementation which concatenates the Step class name and the object hashcode.

The default behaviour may fit simple use-cases but in order to get clear logs and improve your debugging experience we 
encourage you to Override the getId() and setId() methods and give a meaningful name to your Steps.

NOTE: In case of Duplicated Nodes (see next paragraph), implementation of this two methods and supplying a meaningful name 
to your Step is required. Duplicating Steps that don't implement the Identity interface will lead to a runtime exception.

### Duplicated Nodes
In order to maximize CPU usage, Steeping enables consumers to split the workload to multiple Threads. 

Consumers just need to specify the number of Steps nodes and internally Stepping will create the corresponding number of threads 
that will work in-parallel on the data inorder to increase throughput:

```java
public class MyStep implements Step {

    public StepConfig getConfig() {
        stepConfig.setNumOfNodes(3);//Internally Stepping will create 3 Threads to handle load
        return stepConfig;
    }
}
```

As mentioned above consumers can specify their own policy.

As mentioned in the "Step Identity" section, when duplicating Steps it is mandatory to implement the getId() and setId() 
methods and supply a meaningful name to your Step.

Stepping will make sure to create new instances of the duplicated Step and give each one the name supplied by you via the getId()
method with an additional suffix: .<INCREMENTAL-STEP_NUMBER>. i.e. 'myCustomStep.2'

In use-cases where you need to create an in-memory state at initialization phase of the Step, you should do it as part of the init()
method and not in the constructor because When duplicating Steps Stepping creates new instances of your Class by duplicating 
the object but it does so without serializing its in-memory state.


### Performance Sampler
Debugging can be not so trivial in an event driven system as Stepping. 
To facilitate the debugging process, Steeping supplies a special builtin Step (by default not enabled) called PerfSamplerStep designed to help developers understand where their 
application spends its time. 

By enabling this Step, Stepping will emit once awhile (configurable) a report that specify the
time spent on each method, this way, in case of slowness it will be easier to detect the bad behaviour.

Internally Stepping makes use of [perf-sampler](https://github.com/imperva/perf-sampler) an Open Source project and
integrate it within its infrastructure.

PerformanceSamplerStep can be enabled and configured via AlgoConfig in your Algo class:

```java
public AlgoConfig getConfig() {
        AlgoConfig algoConfig = new AlgoConfig();
        algoConfig.getPerfSamplerStepConfig().setEnable(true);
        algoConfig.getPerfSamplerStepConfig().setReportInterval(60);//Sets the periodic interval to emit the report in seconds
        algoConfig.getPerfSamplerStepConfig().setPackages("mycompany.myproj.myclass,mycompany.myproj2.myclass");
        //Required: A comma delimited string that specifies the packages to sample. If not supplied an Exception will be throw
        return algoConfig;
    }
```

Currently the report will be output its findings in the default Logger.

# Getting Help
If you have questions about the library, please be sure to check out the API documentation. 
If you still have questions, reach out me via mail gabi.beyo@imperva.com.
  
