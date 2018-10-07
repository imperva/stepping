package infra.KafkaExternalResource;

import Stepping.Data;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.*;
import java.util.stream.Collectors;

public class KafkaProducer {

    private Producer<Integer, String> producer;
    private List<String> topics;
    private String partitionKey;
    private Gson gson;
    private String id;


    public KafkaProducer(KafkaProducerConfig config) {
        gson = new Gson();
        id = config.getId();
        topics = config.getTopics();
        partitionKey = config.getPartitionKey();
        producer = new org.apache.kafka.clients.producer.KafkaProducer<>(convert(config));
    }

    public void send(Data messages){
        System.out.println(String.format("Going to send message to Kafka from %s: %s", id, messages.getValue().toString()));
        Map<Integer, String> idToListOfMessages = convert((List<JsonObject>) messages.getValue());
        final Integer[] currentCustomer = {-1};
        topics.forEach(topic -> idToListOfMessages.forEach((key, message) -> {
            try {
                long time = System.currentTimeMillis();
                currentCustomer[0] = key;
                final ProducerRecord<Integer, String> record = new ProducerRecord<>(topic, key, message);
                producer.send(record);
                long elapsedTime = System.currentTimeMillis() - time;
                System.out.printf("sent record(key=%s value=%s) " +
                                "time=%d",
                        record.key(), record.value(), elapsedTime);
            } catch (Exception e){
                System.out.println(String.format("Failed to send message: %s. Error: %s", message, e.getMessage()));
                System.out.println(String.format("Failed to send message: %s. Error: %s", message, e.getMessage()));
            } finally {
                System.out.println(String.format("Finishing to send message to Kafka for customer hash %s from %s", currentCustomer[0], id));
            }
        }));
    }

    private Map<Integer, String> convert(List<JsonObject> messages) {
        try {
            Map<Integer, List<JsonObject>> idToListOfMessages = messages.stream()
                    .filter(jsonObject -> jsonObject.has(partitionKey) && jsonObject.get(partitionKey) != null)
                    .collect(Collectors.groupingBy((JsonObject jsonObject) -> jsonObject.get(partitionKey).getAsString().hashCode(), Collectors.mapping(jsonObject -> jsonObject, Collectors.toList())));
            return idToListOfMessages.entrySet().stream()
                    .map(idToMessagesEntry -> new AbstractMap.SimpleEntry<>(idToMessagesEntry.getKey(), gson.toJson(idToMessagesEntry.getValue())))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue
                    ));
        } catch (Exception e) {
            System.out.println(e);
            return new HashMap<>();
        }
    }

    private Properties convert(KafkaProducerConfig config) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBrokerHost());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, config.getClientId());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return props;
    }

    public void shutdown() {
        if (producer != null) {
            producer.flush();
            producer.close();
        }
    }
}
