package alogs;

import Stepping.IAlgo;
import Stepping.IRunning;
import Stepping.Stepping;
import alogs.etlalgo.ETLAlgo;
import kafka.KafkaMessageHandler;
import java.util.Collections;

public class Main {
    public static void main(String[] args)
    {
        IAlgo etlAlgo = new ETLAlgo();

        //* TBD
        Stepping stepping = new Stepping();
        stepping.add(new ETLAlgo());
        stepping.init();

        IRunning messageHandler = new KafkaMessageHandler<String>(
                "consumer".hashCode(),
                "etlGroup",
                Collections.singletonList("damEngineRawData"),
                etlAlgo);
    }

}
