package alogs;

import Stepping.Data;
import Stepping.Stepping;
import Stepping.IAlgo;
import alogs.etlalgo.ETLAlgo;
import infra.KafkaMessengerWrapper;
import infra.StubMessengerWrapper;

public class Main {
    public static void main(String[] args) {

        IAlgo etlAlgo = new ETLAlgo();
        StubMessengerWrapper messengerWrapper = new StubMessengerWrapper(etlAlgo);
        etlAlgo.setMessenger(messengerWrapper);

        //* TBD
        Stepping stepping = new Stepping();
        stepping.register(etlAlgo);


        messengerWrapper.init();


        Runtime.getRuntime().addShutdownHook(new Thread(() -> ((ETLAlgo) etlAlgo).shutdown()));

        Thread t = new Thread(new Thread(() -> {
        while (true) {
            System.out.println("SENDINGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
            etlAlgo.newDataArrived(new Data<Object>());
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        }));
        t.start();
    }
}
