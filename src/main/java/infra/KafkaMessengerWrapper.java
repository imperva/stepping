package infra;

import Stepping.Data;
import Stepping.IAlgo;
import Stepping.IMessenger;

public class KafkaMessengerWrapper implements IMessenger {
    private KafkaConsumer kafkaConsumer;
    private KafkaProducer kafkaProducer;

    public KafkaMessengerWrapper(IAlgo iAlgo) {
        this.kafkaConsumer = new KafkaConsumer(1, "", null, message -> iAlgo.newDataArrived(new Data<>(message.getValue())));
        this.kafkaProducer = new KafkaProducer();
    }

    @Override
    public void emit(Data data) {
        Message message = new Message();
        message.setValue(data.getValue());
        kafkaProducer.send(message);
    }

}
