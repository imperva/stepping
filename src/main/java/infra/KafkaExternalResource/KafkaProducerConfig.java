package infra.KafkaExternalResource;

import java.util.List;

public class KafkaProducerConfig {

    private String id;
    private String algo;
    private String clientId;
    private List<String> topics;
    private String brokerHost;
    private String partitionKey;

    public KafkaProducerConfig(String id, String algo, String clientId, List<String> topics, String brokerHost, String partitionKey) {
        this.id = id;
        this.algo = algo;
        this.clientId = clientId;
        this.topics = topics;
        this.brokerHost = brokerHost;
        this.partitionKey = partitionKey;
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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
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

    public String getPartitionKey() {
        return partitionKey;
    }

    public void setPartitionKey(String partitionKey) {
        this.partitionKey = partitionKey;
    }
}
