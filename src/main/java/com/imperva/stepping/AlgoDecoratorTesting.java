package com.imperva.stepping;

import java.util.List;
import java.util.function.BiFunction;

public class AlgoDecoratorTesting extends AlgoDecorator {
    private Algo algo;
    private  ContainerRegistrar containerRegistrar;
    private  StepConfig stepConfig;
    private List<String> subjects;
    private BiFunction<Data, String, Boolean> onSubjectUpdate;

    public AlgoDecoratorTesting(Algo algo) {
        super(algo);
        this.algo = algo;
    }

    public AlgoDecoratorTesting(Algo algo, ContainerRegistrar containerRegistrar, StepConfig stepConfig, List<String> subjects,  BiFunction<Data,String, Boolean> onSubjectUpdate) {
        super(algo);
        this.algo = algo;
        this.containerRegistrar = containerRegistrar;
        this.stepConfig = stepConfig;
        this.subjects = subjects;
        this.onSubjectUpdate = onSubjectUpdate;
    }


    AlgoDecoratorTesting(Algo algo, String c) {
        super(algo);
    }


    @Override
    public ContainerRegistrar containerRegistration() {
        containerRegistrar.add(new TestingListenerStep(subjects, onSubjectUpdate, stepConfig));
        return containerRegistrar;
    }
}
