package alogs;

import Stepping.Stepping;
import Stepping.IAlgo;
import alogs.etlalgo.ETLAlgo;
import infra.KafkaMessengerWrapper;

public class Main {
    public static void main(String[] args) {

        IAlgo etlAlgo = new ETLAlgo();
        KafkaMessengerWrapper kafkaMessengerWrapper = new KafkaMessengerWrapper(etlAlgo);
        etlAlgo.setMessenger(kafkaMessengerWrapper);

        //* TBD
        Stepping stepping = new Stepping();
        stepping.register(etlAlgo);


        kafkaMessengerWrapper.init();

    }
}
