package com.imperva.stepping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.function.BiFunction;

class AlgoDecoratorLauncher extends AlgoDecorator {
    private final Logger logger = LoggerFactory.getLogger(AlgoDecoratorLauncher.class);
    private Algo algo;//todo not in use
    private ContainerRegistrar containerRegistrar;
    private List<String> subjects;
    private BiFunction<Data, String, Boolean> onSubjectUpdate;


    AlgoDecoratorLauncher(Algo algo, ContainerRegistrar containerRegistrar, List<String> subjects, BiFunction<Data, String, Boolean> onSubjectUpdate) {
        super(algo);
        this.algo = algo;
        this.containerRegistrar = containerRegistrar;
        this.subjects = subjects;
        this.onSubjectUpdate = onSubjectUpdate;
    }

    @Override
    public ContainerRegistrar containerRegistration() {
        containerRegistrar.add(new SystemStepLauncherListener(subjects, onSubjectUpdate));
        return containerRegistrar;
    }

    @Override
    public void init() {
        algo.init();
    }

    @Override
    public void onTickCallBack() {
        algo.onTickCallBack();
    }

    @Override
    public AlgoConfig getConfig() {
        return algo.getConfig();
    }


    @Override
    public void close() {
        try {
            algo.close();
        } catch (IOException e) {
            logger.error("Failed closing AlgoDecoratorLauncher", e);
        }
    }
}
