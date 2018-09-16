package infra.KafkaExternalResource;

import Stepping.Data;
import Stepping.IMessenger;
import com.google.gson.JsonObject;

import java.util.List;

public class KafkaMessengerWrapper implements IMessenger<List<JsonObject>> {
    private KafkaConsumer kafkaConsumer;
    private KafkaProducer kafkaProducer;
    private KafkaConfig kafkaConfig;

    public KafkaMessengerWrapper(KafkaConfig kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
        init();
    }

    @Override
    public void init() {
        this.kafkaConsumer = new KafkaConsumer(kafkaConfig.getConsumerConfig());
        this.kafkaProducer = new KafkaProducer(kafkaConfig.getProducerConfig());
    }

    @Override
    public void emit(Data<List<JsonObject>> data) {
        kafkaProducer.send(data);
    }

    @Override
    public Data<List<JsonObject>> fetching() {
        return this.kafkaConsumer.fetch();
    }

    @Override
    public void shutdown() {
        kafkaConsumer.shutdown();
    }

}
