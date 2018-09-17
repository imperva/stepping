package infra.KafkaExternalResource;

import Stepping.Data;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class KafkaProducer {

    private Producer<Integer, String> producer;
    private List<String> topics;
    private String partitionKey;
    private Gson gson;


    public KafkaProducer(KafkaProducerConfig config) {
        gson = new Gson();
        topics = config.getTopics();
        partitionKey = config.getPartitionKey();
        producer = new org.apache.kafka.clients.producer.KafkaProducer<>(convert(config));
    }

    public void send(Data<List<JsonObject>> messages){
        System.out.println(String.format("Going to send message to Kafka: %s", messages.toString()));
        Map<Integer, String> idToListOfMessages = convert(messages);
        topics.forEach(topic -> {
            idToListOfMessages.forEach((key, message) -> {
                try {
                    long time = System.currentTimeMillis();
                    final ProducerRecord<Integer, String> record = new ProducerRecord<>(topic, key, message);
                    RecordMetadata metadata = producer.send(record).get();
                    long elapsedTime = System.currentTimeMillis() - time;
                    System.out.printf("sent record(key=%s value=%s) " +
                                    "meta(partition=%d, offset=%d) time=%d",
                            record.key(), record.value(), metadata.partition(),
                            metadata.offset(), elapsedTime);
                } catch (Exception e){
                    System.out.println(String.format("Failed to send message: %s. Error: %s", message, e.getMessage()));
                    System.out.println(String.format("Failed to send message: %s. Error: %s", message, e.getMessage()));
                }
            });
        });
    }

    private Map<Integer, String> convert(Data<List<JsonObject>> message) {
        Map<Integer, List<JsonObject>> idToListOfMessages = message.getValue().stream()
                .filter(jsonObject -> jsonObject.has(partitionKey) && jsonObject.get(partitionKey) != null)
                .collect(Collectors.groupingBy((JsonObject jsonObject) -> jsonObject.get(partitionKey).getAsString().hashCode(), Collectors.mapping(jsonObject -> jsonObject, Collectors.toList())));
        return idToListOfMessages.entrySet().stream()
                .map(idToMessagesEntry -> new AbstractMap.SimpleEntry<>(idToMessagesEntry.getKey(), gson.toJson(idToMessagesEntry.getValue())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    private Properties convert(KafkaProducerConfig config) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBrokerHost());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, config.getClientId());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return props;
    }
}
