package alogs;

import Stepping.Data;
import Stepping.Stepping;
import Stepping.IMessenger;
import alogs.etlalgo.ETLAlgo;
import infra.KafkaConsumer;
import infra.KafkaProducer;
import Stepping.IExternalDataReceiver;
import Stepping.AlgoBase;
import infra.Message;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        ETLAlgo etlAlgo = new ETLAlgo();

        new Stepping().register(etlAlgo, new IMessenger() {

            private KafkaConsumer kafkaConsumer;
            private KafkaProducer kafkaProducer;
            @Override
            public void init() {
                kafkaConsumer = new KafkaConsumer(1,"",null,null);
                kafkaProducer = new KafkaProducer();
            }

            @Override
            public void fetching(IExternalDataReceiver dataReceiver) {
                //fetching data

                //on data arrives

                dataReceiver.newDataArrived(new Data<Object>());
            }


            @Override
            public void emit(Data data) {
                kafkaProducer.send(new Message());
            }
        }).go();

        Thread.sleep(10000);

        etlAlgo.close();
        //Runtime.getRuntime().addShutdownHook(new Thread(() -> ((AlgoBase) etlAlgo).shutdown()));
    }
}
