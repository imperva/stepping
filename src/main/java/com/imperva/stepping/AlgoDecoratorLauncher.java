package com.imperva.stepping;

import java.util.List;
import java.util.function.BiFunction;

public class AlgoDecoratorLauncher extends AlgoDecorator {
    private Algo algo;//todo not in use
    private  ContainerRegistrar containerRegistrar;
    private  StepConfig stepConfig;
    private List<String> subjects;
    private BiFunction<Data, String, Boolean> onSubjectUpdate;

    public AlgoDecoratorLauncher(Algo algo) {
        super(algo);
        this.algo = algo;
    }

    public AlgoDecoratorLauncher(Algo algo, ContainerRegistrar containerRegistrar, StepConfig stepConfig, List<String> subjects, BiFunction<Data,String, Boolean> onSubjectUpdate) {
        super(algo);
        this.algo = algo;
        this.containerRegistrar = containerRegistrar;
        this.stepConfig = stepConfig;
        this.subjects = subjects;
        this.onSubjectUpdate = onSubjectUpdate;
    }


    AlgoDecoratorLauncher(Algo algo, String c) {
        super(algo);
    }


    @Override
    public ContainerRegistrar containerRegistration() {
        containerRegistrar.add(new TestingListenerStep(subjects, onSubjectUpdate, stepConfig));
        return containerRegistrar;
    }
}
