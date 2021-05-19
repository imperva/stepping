package com.imperva.stepping;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


class SteppingLauncherTest {
    Algo simpleAlgo;

    @BeforeEach
    void setup() {
        simpleAlgo = new Algo() {
            @Override
            public void init() {
            }

            @Override
            public ContainerRegistrar containerRegistration() {
                return new ContainerRegistrar();
            }

            @Override
            public void onTickCallBack() {
            }

            @Override
            public void close() throws IOException {
            }

            @Override
            public AlgoConfig getConfig() {
                AlgoConfig algoConfig = new AlgoConfig();
                algoConfig.setInitStatCollector(true);
                algoConfig.setStatReportReleaseTimeout(10);
                return algoConfig;
            }
        };
    }


    @Test
    void launcher_condition_satisfied() {

        Step step = new Step() {
            private Container cntr;
            private Shouter shouter;

            @Override
            public void listSubjectsToFollow(Follower follower) {
                follower.follow("STARTED", (data -> {
                    shouter.shout("ARRIVED", new Data(""));
                }));
            }

            @Override
            public void init(Container cntr, Shouter shouter) {
                this.cntr = cntr;
                this.shouter = shouter;
            }

            @Override
            public void onKill() {
            }
        };


        LauncherResults launcherResults = new SteppingLauncher()
                .withAlgo(simpleAlgo)
                .withStep(step)
                .withShout("STARTED", new Data(""))
                .stopOnSubject("ARRIVED")
                .launch();

        Data res = launcherResults.get("ARRIVED");
        Assertions.assertNotNull(res);
    }

    @Test
    void launcher_condition_satisfied2() {

        Step step = new Step() {
            private Container cntr;
            private Shouter shouter;

            @Override
            public void listSubjectsToFollow(Follower follower) {
                follower.follow("STARTED", (data -> {
                    shouter.shout("ARRIVED", new Data(""));
                    shouter.shout("ARRIVED2", new Data(""));
                }));
            }

            @Override
            public void init(Container cntr, Shouter shouter) {
                this.cntr = cntr;
                this.shouter = shouter;
            }

            @Override
            public void onKill() {
            }
        };


        LauncherResults launcherResults = new SteppingLauncher()
                .withAlgo(simpleAlgo)
                .withStep(step)
                .withShout("STARTED", new Data(""))
                .stopOnSubject("ARRIVED")
                .stopOnSubject("ARRIVED2")
                .launch();

        Data res = launcherResults.get("ARRIVED");
        Data res2 = launcherResults.get("ARRIVED2");
        Assertions.assertNotNull(res);
        Assertions.assertNotNull(res2);
    }

    @Test
    void launcher_return_expected_value() {

        Step step = new Step() {
            private Container cntr;
            private Shouter shouter;

            @Override
            public void listSubjectsToFollow(Follower follower) {
                follower.follow("STARTED", (data -> {
                    shouter.shout("ARRIVED", new Data((int) data.getValue() + 1));
                }));
            }

            @Override
            public void init(Container cntr, Shouter shouter) {
                this.cntr = cntr;
                this.shouter = shouter;
            }

            @Override
            public void onKill() {
            }
        };


        LauncherResults launcherResults = new SteppingLauncher()
                .withAlgo(simpleAlgo)
                .withStep(step)
                .withShout("STARTED", new Data(1))
                .stopOnSubject("ARRIVED")
                .launch();

        Data res = launcherResults.get("ARRIVED");
        Assertions.assertEquals(2, (int) res.getValue());
    }

    @Test
    void launcher_stats_test() {

        Step step = new Step() {
            private Container cntr;
            private Shouter shouter;

            @Override
            public void listSubjectsToFollow(Follower follower) {
                follower.follow("STARTED", this::handler);
            }

            void handler(Data d){
                System.out.println("here");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void init(Container cntr, Shouter shouter) {
                this.cntr = cntr;
                this.shouter = shouter;
            }

            @Override
            public void onKill() {
            }

            @Override
            public String getId() {
                return "launcher_stats_test";
            }

            @Override
            public StepConfig getConfig() {
                StepConfig stepConfig = new StepConfig();
                stepConfig.setStatEnabledForStep(true);
                return stepConfig;
            }
        };


        List<String> datas = new ArrayList<>();
        datas.add("Hello");
        datas.add("Hello");
        datas.add("Hello");
        Data d = new Data(datas);
        LauncherResults launcherResults = new SteppingLauncher()
                .withAlgo(simpleAlgo)
                .withStep(step)
                .withShout("STARTED", d, 3)
                .stopOnSubject(BuiltinSubjectType.STEPPING_STEPS_STATISTICS_READY.name())
                .launch();

        Data res = launcherResults.get(BuiltinSubjectType.STEPPING_STEPS_STATISTICS_READY.name()); //TODO stats support re-occurrences and remove timeout
        StatisticsReport statistic = ((StatisticsReport)res.getValue());
        Assertions.assertEquals(0, statistic.getLatestQSize());
        Assertions.assertEquals(3, statistic.getAvgChunkSize());
        Assertions.assertTrue(statistic.getAvgProcessingTime() < 1500.0 && statistic.getAvgProcessingTime() > 950);
    }

    @Test
    void launcher_timeout_exception() {
        Step step = new Step() {
            private Container cntr;
            private Shouter shouter;

            @Override
            public void listSubjectsToFollow(Follower follower) {
                follower.follow("STARTED", (data -> {
                    shouter.shout("ARRIVED", new Data((int) data.getValue() + 1));
                }));
            }

            @Override
            public void init(Container cntr, Shouter shouter) {
                this.cntr = cntr;
                this.shouter = shouter;
            }

            @Override
            public void onKill() {
            }
        };
        Assertions.assertThrows(SteppingLauncherTimeoutException.class, () -> {
            new SteppingLauncher()
                    .withAlgo(simpleAlgo)
                    .withStep(step)
                    .stopOnSubject("ARRIVED")
                    .withTimeout(4000)
                    .launch();
        });
    }

    @Test
    void launcher_registrar() {
        Step step = new Step() {
            private Container cntr;
            private Shouter shouter;

            @Override
            public void listSubjectsToFollow(Follower follower) {
                follower.follow("STARTED", (data -> {
                    shouter.shout("ARRIVED", new Data(cntr.getById("STUB")));
                }));
            }

            @Override
            public void init(Container cntr, Shouter shouter) {
                this.cntr = cntr;
                this.shouter = shouter;
            }

            @Override
            public void onKill() {
            }
        };


        ContainerRegistrar containerRegistrar = new ContainerRegistrar();
        containerRegistrar.add("STUB", "STUB_VAL");
        containerRegistrar.add(step);

        LauncherResults launcherResults = new SteppingLauncher()
                .withAlgo(simpleAlgo)
                .stopOnSubject("ARRIVED")
                .withShout("STARTED", new Data(""))
                .withContainerRegistrar(containerRegistrar)
                .launch();


        Data res = launcherResults.get("ARRIVED");
        Assertions.assertEquals("STUB_VAL", res.getValue().toString());
    }
}
