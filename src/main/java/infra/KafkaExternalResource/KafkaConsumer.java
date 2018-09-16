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
    private final int id;
    private MessageConverter messageConverter;
    private boolean shouldRun;

    public KafkaConsumer(int id, String groupId, List<String> topics) {
        this.shouldRun = true;
        this.id = id;
        this.topics = topics;
        messageConverter = new MessageConverter();
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.100.65.25:9093");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        //props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, "example.CustomAssignor");
        this.consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(props);
    }

    public Data<JsonObject> fetch() {
        try {
            consumer.subscribe(topics, new ConsumerRebalanceListener() {
                @Override
                public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                    System.out.println();

                }

                @Override
                public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
//                    consumer.seekToBeginning(Arrays.asList(new TopicPartition(topics.get(0), 0)));
                    System.out.println();
                }
            });

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
            return new Data(allValues);

        } catch (WakeupException e) {
            return null;
        } finally {
            consumer.close();
        }
    }

    public void shutdown() {
        consumer.wakeup();
        shouldRun = false;
    }
}
