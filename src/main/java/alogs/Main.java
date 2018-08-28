package alogs;

import alogs.etlalgo.ETLAlgo;
import kafka.KafkaMessageHandler;
import messagehandlers.IMessageHandler;

import java.util.Arrays;
import java.util.Collections;

public class Main {
    public static void main(String[] args)
    {

        IAlgo etlAlgo = new ETLAlgo();
        AlgoInfraConfig algoInfraConfig = etlAlgo.init();
        IMessageHandler messageHandler = new KafkaMessageHandler<String>(
                "consumer".hashCode(),
                "etlGroup",
                Collections.singletonList("damEngineRawData"),
                etlAlgo);
    }

}
