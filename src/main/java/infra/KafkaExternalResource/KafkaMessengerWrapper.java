package infra.KafkaExternalResource;

import Stepping.Data;
import Stepping.IMessenger;

import java.io.IOException;

public class KafkaMessengerWrapper implements IMessenger {
    private KafkaConsumer kafkaConsumer;
    private KafkaProducer kafkaProducer;
    private KafkaConfig kafkaConfig;

    public KafkaMessengerWrapper(KafkaConfig kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
        init();
    }

    private void init() {
        this.kafkaConsumer = new KafkaConsumer(kafkaConfig.getConsumerConfig());
        this.kafkaProducer = new KafkaProducer(kafkaConfig.getProducerConfig());
    }

    @Override
    public void emit(Data data) {
        kafkaProducer.send(data);
    }

    @Override
    public Data fetching() {
        return this.kafkaConsumer.fetch();
    }



    @Override
    public void close() {
        kafkaConsumer.shutdown();
        kafkaProducer.shutdown();
    }
}
