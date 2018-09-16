package infra.runners;

import Stepping.Stepping;
import alogs.etlalgo.ETLAlgo;
import infra.KafkaExternalResource.KafkaMessengerWrapper;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        ETLAlgo etlAlgo = new ETLAlgo();

        new Stepping().register(etlAlgo, new KafkaMessengerWrapper()).go();

        Thread.sleep(10000);

        etlAlgo.close();
        //Runtime.getRuntime().addShutdownHook(new Thread(() -> ((AlgoBase) etlAlgo).shutdown()));
    }
}
