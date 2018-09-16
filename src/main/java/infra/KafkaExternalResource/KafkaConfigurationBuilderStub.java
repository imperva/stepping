package infra.KafkaExternalResource;

import infra.IdGenerator;

import java.util.*;

public class KafkaConfigurationBuilderStub implements KafkaConfigurationBuilder {

    @Override
    public Map<String, List<KafkaConfig>> getConfig(IdGenerator idGenerator) {
        Map<String, List<KafkaConfig>> config = new HashMap<>();
        List<KafkaConfig> etlConfig = new ArrayList<>();
        KafkaConsumerConfig consumerConfig = new KafkaConsumerConfig(idGenerator.get(), "ETL", "ETL", Collections.singletonList("b17-events"), "10.100.65.12:9093");
        KafkaProducerConfig producerConfig = new KafkaProducerConfig(idGenerator.get(), "ETL", "ETL", Collections.singletonList("etl-aggregation"), "10.100.65.12:9093", "customerId");
        etlConfig.add(new KafkaConfig(consumerConfig, producerConfig));
        config.put("ETL", etlConfig);
        return config;
    }

}
