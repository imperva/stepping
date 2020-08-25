package com.imperva.stepping;

import java.util.List;
import java.util.function.BiFunction;

class AlgoDecoratorLauncher extends AlgoDecorator {
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
        containerRegistrar.add(new LauncherListenerStep(subjects, onSubjectUpdate));
        return containerRegistrar;
    }
}
