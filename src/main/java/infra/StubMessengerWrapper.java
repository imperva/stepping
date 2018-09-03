package infra;

import Stepping.Data;
import Stepping.IAlgo;
import Stepping.IMessenger;

public class StubMessengerWrapper implements IMessenger {
    private KafkaConsumer kafkaConsumer;
    private KafkaProducer kafkaProducer;
    private IAlgo iAlgo;

    public StubMessengerWrapper(IAlgo iAlgo) {
        this.iAlgo = iAlgo;
    }


    public void init() {

    }

    @Override
    public void emit(Data data) {

        System.out.println("StubMessengerWrapper: Data published to Kafka");
    }

}
