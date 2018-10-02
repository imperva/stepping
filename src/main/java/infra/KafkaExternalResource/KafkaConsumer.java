package infra.KafkaExternalResource;

import Stepping.Data;
import com.google.gson.JsonObject;
import infra.MessageConverter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class KafkaConsumer {

    private org.apache.kafka.clients.consumer.KafkaConsumer<String, String> consumer;
    private final List<String> topics;
    private final String id;
    private MessageConverter messageConverter;
    private boolean shouldRun;

    public KafkaConsumer(KafkaConsumerConfig config) {
        this.shouldRun = true;
        this.id = config.getId();
        this.topics = config.getTopics();
        messageConverter = new MessageConverter();
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBrokerHost());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, config.getGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);
        consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(props);
        //props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, "example.CustomAssignor");
        consumer.subscribe(topics, new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                System.out.println("Partition Revoked");
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                System.out.println("Partition Assigned");

                //todo for testing remove it when finishing to test
                consumer.seekToBeginning(Arrays.asList(new TopicPartition(topics.get(0), 0), new TopicPartition(topics.get(0), 1), new TopicPartition(topics.get(0), 2)));
                System.out.println("Reset offset of all partitions");
            }
        });
    }

    public Data fetch() {
        try {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofDays(30L));
            if (!shouldRun) return null;

            List<String> values = new ArrayList<>();
            for (ConsumerRecord<String, String> record : records) {
                Map<String, Object> data = new HashMap<>();
                data.put("partition", record.partition());
                data.put("offset", record.offset());
                data.put("value", record.value());
                values.add(record.value());
                System.out.println(this.id + ": " + data);
                System.out.println(this.id + ": " + Thread.currentThread().getId());
            }
            List<JsonObject> allValues = !values.isEmpty()? values.stream()
                    .map(val -> messageConverter.convert(val))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()): new ArrayList<>();
            if (allValues.size() > 0) {
                System.out.println(String.format("Read new data from Kafka: %s", allValues.toString()));
            }
            return new Data(allValues);

        } catch (WakeupException e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        consumer.close(); //todo ??
        shouldRun = false;
    }
}
