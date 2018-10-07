package infra.KafkaExternalResource;

public class KafkaConfig {

    private KafkaConsumerConfig consumerConfig;
    private KafkaProducerConfig producerConfig;

    public KafkaConfig(KafkaConsumerConfig consumerConfig, KafkaProducerConfig producerConfig) {
        this.consumerConfig = consumerConfig;
        this.producerConfig = producerConfig;
    }

    public KafkaConsumerConfig getConsumerConfig() {
        return consumerConfig;
    }

    public void setConsumerConfig(KafkaConsumerConfig consumerConfig) {
        this.consumerConfig = consumerConfig;
    }

    public KafkaProducerConfig getProducerConfig() {
        return producerConfig;
    }

    public void setProducerConfig(KafkaProducerConfig producerConfig) {
        this.producerConfig = producerConfig;
    }
}
