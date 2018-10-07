package infra.KafkaExternalResource;

import java.util.List;

public class KafkaConsumerConfig {

    private String id;
    private String algo;
    private String groupId;
    private List<String> topics;
    private String brokerHost;

    public KafkaConsumerConfig(String id, String algo, String groupId, List<String> topics, String brokerHost) {
        this.id = id;
        this.algo = algo;
        this.groupId = groupId;
        this.topics = topics;
        this.brokerHost = brokerHost;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlgo() {
        return algo;
    }

    public void setAlgo(String algo) {
        this.algo = algo;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public String getBrokerHost() {
        return brokerHost;
    }

    public void setBrokerHost(String brokerHost) {
        this.brokerHost = brokerHost;
    }
}
