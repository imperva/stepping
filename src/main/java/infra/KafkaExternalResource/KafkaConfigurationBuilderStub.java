package infra.KafkaExternalResource;

import java.util.ArrayList;
import java.util.List;

public class KafkaConfigurationBuilderStub implements KafkaConfigurationBuilder {


    @Override
    public List<KafkaConfig> getConfig() {
        List<KafkaConfig> config = new ArrayList<>();
        return config;
    }
}
