package infra.runners;

import Stepping.Stepping;
import alogs.etlalgo.ETLAlgo;
import infra.AlgoId;
import infra.ConfigurationBuilder;
import infra.IdGenerator;
import infra.KafkaExternalResource.KafkaConfig;
import infra.KafkaExternalResource.KafkaConfigurationBuilderStub;
import infra.KafkaExternalResource.KafkaMessengerWrapper;
import infra.RandomIdGenerator;

import java.util.List;
import java.util.Map;

public class Main {

    private static Stepping stepping = new Stepping();

    public static void main(String[] args) throws InterruptedException {

        IdGenerator idGenerator = new RandomIdGenerator();
        ConfigurationBuilder<KafkaConfig> configurationBuilder = new KafkaConfigurationBuilderStub();

        Map<String, List<KafkaConfig>> config = configurationBuilder.getConfig(idGenerator);
        config.forEach((algo, messengerConfigs) -> {
            if (AlgoId.ETL.name().equals(algo)) {
                messengerConfigs.forEach(messengerConfig -> {
                    ETLAlgo etlAlgo = new ETLAlgo();
                    stepping.register(etlAlgo, new KafkaMessengerWrapper(messengerConfig)).go();
                    Runtime.getRuntime().addShutdownHook(new Thread((etlAlgo)::shutdown));
                });
            }
        });
    }
}
