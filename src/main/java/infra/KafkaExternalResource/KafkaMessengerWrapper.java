package infra.KafkaExternalResource;

import Stepping.Data;
import Stepping.IMessenger;
import com.google.gson.JsonObject;
import infra.Message;

public class KafkaMessengerWrapper<T> implements IMessenger {
    private KafkaConsumer kafkaConsumer;
    private KafkaProducer kafkaProducer;

    public KafkaMessengerWrapper() {
    }


    public void init() {
        this.kafkaConsumer = new KafkaConsumer(1, "", null);
        this.kafkaProducer = new KafkaProducer();
    }

    @Override
    public void emit(Data data) {
        Message message = new Message();
        message.setValue(data.getValue());
        kafkaProducer.send(message);
    }

    public Data<JsonObject> fetching() {
        return this.kafkaConsumer.fetch();
    }

    @Override
    public void shutdown() {
        kafkaConsumer.shutdown();
    }

}
