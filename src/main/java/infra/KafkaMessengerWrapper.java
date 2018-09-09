package infra;

import Stepping.*;

public class KafkaMessengerWrapper implements IMessenger {
    private KafkaConsumer kafkaConsumer;
    private KafkaProducer kafkaProducer;
    private AlgoBase iAlgo;

    public KafkaMessengerWrapper(AlgoBase iAlgo) {
        this.iAlgo = iAlgo;
    }


    public void init() {
        this.kafkaConsumer = new KafkaConsumer(1, "", null, message -> iAlgo.newDataArrived(new Data<>(message.getValue())));
        this.kafkaProducer = new KafkaProducer();
    }

    @Override
    public void emit(Data data) {
        Message message = new Message();
        message.setValue(data.getValue());
        kafkaProducer.send(message);
    }

    @Override
    public void fetching(IExternalDataReceiver dataReceiver) {

    }

}
