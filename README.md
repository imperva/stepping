# Stepping 
Stepping is a framework designed to eases the processing of streams of data.
In use cases where we need to implement a data-streaming algorithm or any processing on the data, we need to handle many infrastructure issues.
For example, we need to decide how to split the data processing into different steps, how this division fits with our threading policy, how to handle communication between the different steps, error handling etc.
One of the most important subjects is the Threading Policy of our solution. For example we need to think how many threads to open, have the option to distribute the processing of data to multiple 'executors' in parallel etc.
On top of that we care a lot about the performance of our solution, we want to make sure that the latency added by these infrastructures is minimal as possible. 

Stepping aims to handle many of these aspects so developers can spend their time on the business logic instead of infrastructure and data flow issues. 

# Benefits
The goal of this framework is to solve infrastructure issues encountered when we develop a data-streaming processing solution.
Stepping is an event-driven, multithreaded, thread-safe (lockless) framework that handles common data flow issues, for example:

- Handling concurrency and thread safety
- Expose an easy way to split the entire data-processing logic into small multiple units (steps) making sure that all the units are always busy and efficient  
- Communication between the different parts (steps) of the solution
- Enable consumers to split the processing data to be processed in parallel by different threads (steps)
- Expose an extremely easy API to implement on top of it data-streaming processes  
- Enable customers to implement custom error handling policies

# Dependencies
- log4j

# How it works
Stepping has three main players: Algos, Steps and Subjects.

### Algos
Algos are containers of Steps, each Algo contains a Step or more (there are no limits on the number of steps an Algo can contain).
An Algo is responsible of initiating the steps, handling errors and exposing Stepping's external API to consumers.

### Steps
Steps are contained and initialized by their Algo and containS & execute the data-streaming logic (consumers' business logic).
Each Step register itself to Subject(s) (events) it is interested and the framework makes sure that whenever a Subject is triggered the registered Steps will get it.
Once a Subjects is triggered the relevant Steps get a chance to perform their data processing. 
When data processing stage is done, Steps can notify subscribers and send them the result of the processing stage so they can execute their logic on the data.
Stepping usually runs in the background as service/daemon and executes internally at least two Steps, one acts as the entry-point which reads data from a data source and another Step that acts as an ending-point, the last step of the algorithm (usually writes back the result to a streaming platform or into a DB) 
  

