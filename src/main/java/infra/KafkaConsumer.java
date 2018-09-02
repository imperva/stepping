package infra;

import Stepping.IRunning;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.*;

public class KafkaConsumer<T> extends IRunning {

    private org.apache.kafka.clients.consumer.KafkaConsumer<String, String> consumer;
    private final List<String> topics;
    private final int id;
    private boolean isRunning;
    private ICallback callback;
    private MessageConverter<T> messageConverter;

    public KafkaConsumer(int id, String groupId, List<String> topics, ICallback callback) {
        super("KafkaConsumer" + id, 1, 1);
        this.callback = callback;
        this.isRunning = true;
        this.id = id;
        this.topics = topics;
        messageConverter = new MessageConverter<>();
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.100.65.25:9093");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        //props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, "example.CustomAssignor");
        this.consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(props);

        wakenProcessingUnit();
    }

    @Override
    public void run() {
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

            while (isRunning) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofDays(30L));
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
                Message message = new Message();
                message.setValue(messageConverter.convert(values));
                callback.call(message);
            }
        } catch (WakeupException e) {
            // ignore for shutdown
        } finally {
            consumer.close();
        }
    }

    public void shutdown() {
        consumer.wakeup();
    }
}
