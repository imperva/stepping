package infra.KafkaExternalResource;

import java.util.List;

public class KafkaConfig {

    private int id;
    private String groupId;
    private List<String> topics;
    private String brokerHost;

    public KafkaConfig(int id, String groupId, List<String> topics) {
        this.id = id;
        this.groupId = groupId;
        this.topics = topics;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
