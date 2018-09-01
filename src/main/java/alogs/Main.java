package alogs;

import Stepping.Stepping;
import Stepping.IAlgo;
import alogs.etlalgo.ETLAlgo;
import infra.KafkaMessengerWrapper;

public class Main {
    public static void main(String[] args) {

        IAlgo etlAlgo = new ETLAlgo();

        //* TBD
        Stepping stepping = new Stepping();
        stepping.register(etlAlgo);

        KafkaMessengerWrapper kafkaMessengerWrapper = new KafkaMessengerWrapper(etlAlgo);

    }
}
