package com.imperva.stepping;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Author: Linda Nasredin
 * Date: 17 Sep 2020
 */
class StepDecoratorTest {

    private class Holder<T> {
        T value;
    }

    private class ContainerShouterStep implements Step {
        private Container container;
        private Shouter shouter;

        @Override
        public void init(Container container, Shouter shouter) {
            this.container = container;
            this.shouter = shouter;
        }

        @Override
        public void onKill() {
        }
    }

    private class CounterStep extends ContainerShouterStep {
        protected Counter counter;

        private CounterStep(Counter counter) {
            this.counter = counter;
        }

        @Override
        public void onKill() {
            decrementCounter();
        }

        @Override
        public void onRestate() {
            incrementCounter();
        }

        @Override
        public void onSubjectUpdate(Data data, String subjectType) {
            incrementCounter();
            incrementCounter();
        }

        @Override
        public void onTickCallBack() {
            decrementCounter();
            decrementCounter();
        }

        protected void incrementCounter() {
            counter.increment();
        }

        protected void decrementCounter() {
            counter.decrement();
        }
    }

    /**
     * A step which holds the last data it received
     */
    private class LastDataHolderStep extends ContainerShouterStep {
        private Data lastData;
        private String lastSubjectType;

        @Override
        public void onSubjectUpdate(Data data, String subjectType) {
            lastData = data;
            lastSubjectType = subjectType;

            synchronized (lastData) {
                lastData.notify();
            }
        }
    }

    private class CounterStepWithConfig extends CounterStep {
        private String id;
        protected StepConfig config;

        private CounterStepWithConfig(Counter counter, StepConfig config) {
            super(counter);
            this.config = config;
        }

        @Override
        public StepConfig getConfig() {
            return config;
        }

        @Override
        public void setId(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }
    }

    private class StepWithConfig extends ContainerShouterStep {
        private StepConfig config;

        public StepWithConfig(StepConfig config) {
            this.config = config;
        }

        @Override
        public StepConfig getConfig() {
            return config;
        }

        @Override
        public void onTickCallBack() {
        }
    }

//    private class LongerInitialDelayRunningScheduled extends RunningScheduled {
//
//        protected LongerInitialDelayRunningScheduled(String id, Runnable runnable) {
//            super(id, runnable);
//        }
//
//        @Override
//        protected Pair<Long, Long> calculateDelays(String cronExpression) {
//            Pair<Long, Long> delays = super.calculateDelays(cronExpression);
//            return new Pair<>(delays.getKey() + 60000, delays.getValue()); // add 1 minute to the initial delay
//        }
//    }

    private class SubjectWithCounter implements ISubject {
        private String type;
        private Counter counter;

        public SubjectWithCounter(String type, Counter counter) {
            this.type = type;
            this.counter = counter;
        }

        @Override
        public String getType() {
            return type;
        }

        @Override
        public Data getData() {
            return null;
        }

        @Override
        public void attach(IStepDecorator o) {
            counter.increment();
        }

        @Override
        public void publish(Data data) {
        }

        @Override
        public void publish(Object message) {
        }
    }

    @Test
    void init_with1Arg() {
        Container container = mock(Container.class);
        Shouter shouter = mock(Shouter.class);
        doAnswer(a -> shouter).when(container).getById(eq(BuiltinTypes.STEPPING_SHOUTER.name()));

        StepDecorator decorator = createDoubleStep();
        decorator.init(container, shouter);

        Assertions.assertSame(shouter, ((ContainerShouterStep) decorator.getStep()).shouter);
    }

    @Test
    void init_with2Args() {
        Container container = mock(Container.class);
        Shouter shouter = mock(Shouter.class);

        StepDecorator decorator = createTripleStep();
        decorator.init(container, shouter);

        Assertions.assertSame(container, decorator.container);
        Assertions.assertSame(container, ((StepDecorator) decorator.getStep()).container);
        Assertions.assertSame(container, ((ContainerShouterStep) ((StepDecorator) decorator.getStep()).getStep()).container);

        Assertions.assertSame(shouter, ((ContainerShouterStep) ((StepDecorator) decorator.getStep()).getStep()).shouter);

        Assertions.assertNotNull(decorator.q);
        Assertions.assertNotNull(((StepDecorator) decorator.getStep()).q);
    }

    @Test
    void onRestate() {
        Counter counter = new Counter(0);
        StepDecorator decorator = createCounterTripleStep(counter);

        decorator.onRestate();
        decorator.onRestate();
        Assertions.assertEquals(2, counter.get());
    }

    @Test
    void onKill() {
        Counter counter = new Counter(0);
        StepDecorator decorator = createCounterTripleStep(counter);

        decorator.onKill();
        Assertions.assertEquals(-1, counter.get());
    }

    @Test
    void onSubjectUpdate() {
        Counter counter = new Counter(0);
        StepDecorator decorator = createCounterTripleStep(counter);

        decorator.onSubjectUpdate(mock(Data.class), "subject1");
        Assertions.assertEquals(2, counter.get());
    }

    @Test
    void queueSubjectUpdate() {
        StepDecorator decorator = new StepDecorator(spy(Step.class));
        decorator.init(mock(Container.class), mock(Shouter.class));

        Assertions.assertThrows(SteppingException.class, () -> decorator.queueSubjectUpdate(null, "subject1"));
        Assertions.assertThrows(SteppingException.class, () -> decorator.queueSubjectUpdate(mock(Data.class), null));
        Assertions.assertThrows(SteppingException.class, () -> decorator.queueSubjectUpdate(mock(Data.class), ""));

        decorator.queueSubjectUpdate(mock(Data.class), "subject2");
        decorator.queueSubjectUpdate(mock(Data.class), "subject2");
        Assertions.assertEquals(2, decorator.getQSize());
    }

    @Test
    void offerQueueSubjectUpdate() {
        StepConfig stepConfig = new StepConfig();
        stepConfig.setBoundQueueCapacity(1);

        Step step = mock(Step.class);
        doAnswer(a -> stepConfig).when(step).getConfig();

        StepDecorator decorator = new StepDecorator(step);
        decorator.init(mock(Container.class), mock(Shouter.class));

        Assertions.assertThrows(SteppingException.class, () -> decorator.offerQueueSubjectUpdate(null, "subject1"));
        Assertions.assertThrows(SteppingException.class, () -> decorator.offerQueueSubjectUpdate(mock(Data.class), null));
        Assertions.assertThrows(SteppingException.class, () -> decorator.offerQueueSubjectUpdate(mock(Data.class), ""));

        boolean result = decorator.offerQueueSubjectUpdate(mock(Data.class), "subject2");
        Assertions.assertTrue(result);
        result = decorator.offerQueueSubjectUpdate(mock(Data.class), "subject2");
        Assertions.assertFalse(result);
    }

    @Test
    void clearQueueSubject() {
        StepDecorator decorator = new StepDecorator(spy(Step.class));
        decorator.init(mock(Container.class), mock(Shouter.class));

        decorator.queueSubjectUpdate(mock(Data.class), "subject1");
        decorator.queueSubjectUpdate(mock(Data.class), "subject2");

        decorator.clearQueueSubject();
        Assertions.assertEquals(0, decorator.getQSize());
    }

    @Test
    void onTickCallBack() {
        Counter counter = new Counter(-2);

        Step step = new CounterStep(counter) {
            @Override
            public void onTickCallBack() {
                decrementCounter();
                decrementCounter();
                if (counter.get() == -6)
                    throw new IllegalStateException();
            }
        };
        StepDecorator decorator = new StepDecorator(step);

        decorator.onTickCallBack();
        Assertions.assertEquals(-4, counter.get());

        Assertions.assertThrows(IdentifiableSteppingException.class, decorator::onTickCallBack);
    }

    @Test
    void openDataSink_passMessageToStep() {
        Data data = createNonExpirableData();
        String subjectType = "subject1";

        StepDecorator decorator = createDataHolderDoubleStep();
        decorator.init(mock(Container.class), mock(Shouter.class));

        decorator.q.queue(new Message(data, subjectType));

        new Thread(decorator::openDataSink).start();
        try {
            synchronized (data) {
                data.wait(Consts.WAIT_TIMEOUT_MILLIS);
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException("Interrupted while waiting", ex);
        }

        Assertions.assertSame(data, ((LastDataHolderStep) decorator.getStep()).lastData);
        Assertions.assertEquals(subjectType, ((LastDataHolderStep) decorator.getStep()).lastSubjectType);

        Data newData = createExpirableData(true);
        decorator.q.queue(new Message(newData, subjectType));
        try {
            synchronized (newData) {
                newData.wait(Consts.WAIT_TIMEOUT_MILLIS);
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException("Interrupted while waiting", ex);
        }

        Assertions.assertSame(newData, ((LastDataHolderStep) decorator.getStep()).lastData);
        Assertions.assertEquals(subjectType, ((LastDataHolderStep) decorator.getStep()).lastSubjectType);
    }

    @Test
    void openDataSink_dead() {
        Data data1 = createNonExpirableData();
        String subjectType1 = "subject1";

        Data data2 = createNonExpirableData();
        String subjectType2 = "subject2";

        Data data3 = createNonExpirableData();
        String subjectType3 = Consts.POINSON_PILL_SUBJECT_TYPE;

        Data data4 = createNonExpirableData();
        String subjectType4 = "subject4";

        StepDecorator decorator = createDataHolderDoubleStep();
        decorator.init(mock(Container.class), mock(Shouter.class));

        decorator.q.queue(new Message(data1, subjectType1));
        decorator.q.queue(new Message(data2, subjectType2));
        decorator.q.queue(new Message(data3, subjectType3));
        decorator.q.queue(new Message(data4, subjectType4));

        Assertions.assertThrows(SteppingSystemException.class, decorator::openDataSink);
        Assertions.assertSame(data2, ((LastDataHolderStep) decorator.getStep()).lastData);
    }

    @Test
    void openDataSink_externallyDead() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Counter counter = new Counter(0);
        Step step = new CounterStep(counter) {

            private StepDecorator decorator;

            @Override
            public void init(Container cntr, Shouter shouter) {
            }

            @Override
            public void onKill() {
            }

            @Override
            public void onSubjectUpdate(Data data, String subjectType) {
                super.onSubjectUpdate(data, subjectType);
                if (subjectType.equals("subjectType2"))
                    decorator.dead = true;

            }

            public void setDecorator(StepDecorator decorator) {
                this.decorator = decorator;
            }
        };
        StepDecorator decorator = new StepDecorator(step);
        step.getClass().getMethod("setDecorator", StepDecorator.class).invoke(step, decorator);
        decorator.init(mock(Container.class), mock(Shouter.class));

        decorator.q.queue(new Message(mock(Data.class), "subjectType1"));
        decorator.q.queue(new Message(mock(Data.class), "subjectType2"));
        decorator.q.queue(new Message(mock(Data.class), "subjectType1"));

        decorator.openDataSink();

        Assertions.assertEquals(4, counter.get());
    }

    @Test
    void openDataSink_interrupt() {
        StepDecorator decorator = createDoubleStep();
        decorator.init(mock(Container.class), mock(Shouter.class));

        Thread thread = new Thread(() -> Assertions.assertThrows(SteppingSystemException.class, decorator::openDataSink));
        thread.start();
        thread.interrupt();
    }

    @Test
    void openDataSink_subjectUpdateEvents(){
        Counter counter = new Counter(0);
        StepDecorator decorator = createCounterTripleStep(counter);
        decorator.init(mock(Container.class), mock(Shouter.class));
        decorator.subjectUpdateEvents.put("subject1", (data) -> counter.increment());
        decorator.subjectUpdateEvents.put("subject2", (data) -> { counter.increment(); counter.increment();});

        Data data = createExpirableData(true);
        decorator.q.queue(new Message(data, "subject1"));
        new Thread(decorator::openDataSink).start();
        try {
            synchronized (data) {
                data.wait(Consts.WAIT_TIMEOUT_MILLIS);
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException("Interrupted while waiting", ex);
        }

        // counter was incremented twice by step's onSubjectUpdate and 1 time by SubjectEventUpdate
        Assertions.assertEquals(3, counter.get());
    }

    @Test
    void openDataSink_expirableButNotGrabbable() {
        StepDecorator decorator = createDataHolderDoubleStep();
        decorator.init(mock(Container.class), mock(Shouter.class));

        new Thread(decorator::openDataSink).start();
        Data data1 = createExpirableData(false);
        decorator.q.queue(new Message(data1, Consts.POINSON_PILL_SUBJECT_TYPE));
        Data data2 = createExpirableData(true);
        decorator.q.queue(new Message(data2, "subject1"));
        synchronized (data2) {
            try {
                data2.wait(Consts.WAIT_TIMEOUT_MILLIS);
            } catch (InterruptedException ex) {
                throw new RuntimeException("Interrupted while waiting", ex);
            }
        }

        Assertions.assertSame(data2, ((LastDataHolderStep) decorator.getStep()).lastData);
    }

    @Test
    void openDataSink_timeoutCallback() {
        Counter counter = new Counter(0);
        StepDecorator decorator = createCounterTripleStep(counter);
        decorator.init(mock(Container.class), mock(Shouter.class));

        CyclicBarrier cb = new CyclicBarrier(2);
        decorator.q.queue(new Message(new Data(cb), BuiltinSubjectType.STEPPING_TIMEOUT_CALLBACK.name()));
        new Thread(decorator::openDataSink).start();
        try {
            cb.await();
        } catch (InterruptedException|BrokenBarrierException ex) {
            throw new RuntimeException("Interrupted or broken while awaiting on cyclic barrier", ex);
        }
        Assertions.assertEquals(-2, counter.get());
    }

    @Test
    void openDataSink_cronDelay_changeOnTheFly() {
        Counter counter = new Counter(13);
        StepConfig stepConfig = new StepConfig();
        stepConfig.setRunningInitialDelay(0);
        stepConfig.setRunningPeriodicDelay(500);
        stepConfig.setRunningPeriodicDelayUnit(TimeUnit.MILLISECONDS);
        stepConfig.setEnableTickCallback(true);

        Step step = new CounterStepWithConfig(counter, stepConfig) {
            @Override
            public void onTickCallBack() {
                super.onTickCallBack();
                if (counter.get() < 10)
                    config.setRunningPeriodicCronDelay("0 */1 * * * *"); // every minute
            }
        };
        StepDecorator decorator = new StepDecorator(step);
        String stepId = "step1";
        decorator.getStep().setId(stepId);

        Container cntr = new ContainerDefaultImpl();
        Container cntrPublic = new ContainerService();
        cntrPublic.add(cntr, ContainerService.STEPPING_PRIVATE_CONTAINER);

        decorator.init(cntrPublic, mock(Shouter.class));

        String runningScheduledId = stepId + ContainerService.RUNNING_SCHEDULED;
        final CyclicBarrier cb = new CyclicBarrier(2);
        try (RunningScheduled runningScheduled = new RunningScheduled(runningScheduledId,
                () -> {
                    try {
                        decorator.queueSubjectUpdate(new Data(cb), BuiltinSubjectType.STEPPING_TIMEOUT_CALLBACK.name());
                        cb.await();
                    } catch (Exception|Error ex) {
                        throw new RuntimeException("Caught exception or error", ex);
                    }
                })) {
            runningScheduled.setDelay(stepConfig.getRunningPeriodicDelay(), stepConfig.getRunningInitialDelay(), stepConfig.getRunningPeriodicDelayUnit());
            cntr.add(runningScheduled, runningScheduledId);

            new Thread(decorator::openDataSink).start();
            runningScheduled.awake();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                throw new RuntimeException("Interrupted or while sleeping", ex);
            }
        }

        Assertions.assertEquals(9, counter.get());
    }

    @Test
    void openDataSink_cronDelay_exception() {
        StepConfig stepConfig = new StepConfig();
        stepConfig.setRunningPeriodicCronDelay("*/5 * * * * *");

        StepDecorator decorator = createDoubleConfigStep(stepConfig);
        decorator.init(mock(Container.class), mock(Shouter.class));

        decorator.q.queue(new Message(mock(Data.class), BuiltinSubjectType.STEPPING_TIMEOUT_CALLBACK.name()));
        Assertions.assertThrows(SteppingException.class, decorator::openDataSink);
    }

    @Test
    void openDataSink_cronDelay_exception_withAwait() {
        StepConfig stepConfig = new StepConfig();
        stepConfig.setRunningPeriodicCronDelay("*/5 * * * * *");

        StepDecorator decorator = createDoubleConfigStep(stepConfig);
        decorator.init(mock(Container.class), mock(Shouter.class));

        CyclicBarrier cb = new CyclicBarrier(1);
        decorator.q.queue(new Message(new Data(cb), BuiltinSubjectType.STEPPING_TIMEOUT_CALLBACK.name()));
        Assertions.assertThrows(IdentifiableSteppingException.class, decorator::openDataSink);
    }

    @Test
    void openDataSink_error() {
        Step step = new Step() {
            @Override
            public void init(Container cntr, Shouter shouter) {
            }

            @Override
            public void onKill() {
            }

            @Override
            public void onSubjectUpdate(Data data, String subjectType) {
                throw new AbstractMethodError();
            }
        };
        StepDecorator decorator = new StepDecorator(step);
        decorator.init(mock(Container.class), mock(Shouter.class));

        decorator.q.queue(new Message(mock(Data.class), "subject"));
        Assertions.assertThrows(IdentifiableSteppingError.class, decorator::openDataSink);
    }

    @Test
    void openDataSink_nullMessage() {
        StepDecorator decorator = createDoubleStep();
        decorator.init(mock(Container.class), mock(Shouter.class));

        Assertions.assertThrows(SteppingSystemException.class, () -> decorator.q.queue(null));
    }

    @Test
    void attachSubjects_zeroFollowers() {
        Step step = new Step() {

            @Override
            public void init(Container cntr, Shouter shouter) {
            }

            @Override
            public void onKill() {
            }

            @Override
            public boolean followsSubject(String subjectType) {
                return subjectType.equals("subject1") || subjectType.equals("subject2");
            }
        };
        StepDecorator decorator = new StepDecorator(step);
        Container cntrPublic = new ContainerService();
        decorator.init(cntrPublic, mock(Shouter.class));

        Counter counter = new Counter(0);

        cntrPublic.add(new Identifiable<>(new SubjectWithCounter("subject1", counter), "subject1"));
        cntrPublic.add(new ContainerDefaultImpl(), ContainerService.STEPPING_PRIVATE_CONTAINER);
        cntrPublic.add(new Identifiable<>(new SubjectWithCounter("subject2", counter), "subject2"));
        cntrPublic.add(new Identifiable<>(new SubjectWithCounter("subject3", counter), "subject3"));
        cntrPublic.add(decorator);

        decorator.attachSubjects();

        Assertions.assertEquals(2, counter.get());
    }

    @Test
    void attachSubjects_withFollower_withoutEvent() {
        Step step = new Step() {

            @Override
            public void init(Container cntr, Shouter shouter) {
            }

            @Override
            public void onKill() {
            }

            @Override
            public void listSubjectsToFollow(Follower follower) {
                follower.follow("subject1")
                        .follow("subject2")
                        .follow("subject55");
            }
        };
        StepDecorator decorator = new StepDecorator(step);
        Container cntrPublic = new ContainerService();
        decorator.init(cntrPublic, mock(Shouter.class));

        Counter counter = new Counter(0);

        cntrPublic.add(new Identifiable<>(new SubjectWithCounter("subject1", counter), "subject1"));
        cntrPublic.add(new ContainerDefaultImpl(), ContainerService.STEPPING_PRIVATE_CONTAINER);
        cntrPublic.add(new Identifiable<>(new SubjectWithCounter("subject2", counter), "subject2"));
        cntrPublic.add(new Identifiable<>(new SubjectWithCounter("subject55", counter), "subject55"));

        decorator.attachSubjects();

        Assertions.assertEquals(3, counter.get());
    }

    @Test
    void attachSubjects_withFollower_withEvent() {
        Counter counter = new Counter(0);
        Step step = new CounterStep(counter) {

            @Override
            public void init(Container cntr, Shouter shouter) {
            }

            @Override
            public void onKill() {
            }

            @Override
            public void listSubjectsToFollow(Follower follower) {
                follower.follow("subject1", new SubjectUpdateEvent() {
                    @Override
                    public void onUpdate(Data data) {
                        counter.increment();
                    }
                });
            }
        };
        StepDecorator decorator = new StepDecorator(step);
        Container cntrPublic = new ContainerService();
        decorator.init(cntrPublic, mock(Shouter.class));

        cntrPublic.add(new Identifiable<>(new SubjectWithCounter("subject1", counter), "subject1"));
        cntrPublic.add(new ContainerDefaultImpl(), ContainerService.STEPPING_PRIVATE_CONTAINER);
        cntrPublic.add(new Identifiable<>(new SubjectWithCounter("subject2", counter), "subject2"));

        decorator.attachSubjects();

        Assertions.assertEquals(1, counter.get());
        Assertions.assertEquals(1, decorator.subjectUpdateEvents.size());
    }

    @Test
    void attachSubjects_withFollower_nullSubject() {
        Step step = new Step() {

            @Override
            public void init(Container cntr, Shouter shouter) {
            }

            @Override
            public void onKill() {
            }

            @Override
            public void listSubjectsToFollow(Follower follower) {
                follower.follow("subject1")
                        .follow("subject2")
                        .follow("subject3");
            }
        };
        StepDecorator decorator = new StepDecorator(step);
        Container cntrPublic = new ContainerService();
        decorator.init(cntrPublic, mock(Shouter.class));

        cntrPublic.add(new Identifiable<>(new Subject("subject1"), "subject1"));
        cntrPublic.add(new Identifiable<>(new Subject("subject55"), "subject55"));

        Assertions.assertThrows(SteppingSystemException.class, decorator::attachSubjects);
    }

    @Test
    void followsSubject() {
        Step step = new Step() {

            @Override
            public void init(Container cntr, Shouter shouter) {
            }

            @Override
            public void onKill() {
            }

            @Override
            public boolean followsSubject(String subjectType) {
                return subjectType.equals("subject1");
            }
        };

        StepDecorator decorator = new StepDecorator(step);
        Assertions.assertTrue(decorator.followsSubject("subject1"));
        Assertions.assertFalse(decorator.followsSubject("subject2"));
    }

    @Test
    void private_followsSubject_exception() {
        Step step = new Step() {

            @Override
            public void init(Container cntr, Shouter shouter) {
            }

            @Override
            public void onKill() {
            }

            @Override
            public boolean followsSubject(String subjectType) {
                throw new RuntimeException("mmm");
            }
        };

        StepDecorator decorator = new StepDecorator(step);
        Container cntrPublic = new ContainerService();
        decorator.init(cntrPublic, mock(Shouter.class));

        cntrPublic.add(new Identifiable<>(new Subject("subject1"), "subject1"));

        Assertions.assertThrows(IdentifiableSteppingException.class, decorator::attachSubjects);
    }

    @Test
    void listSubjectsToFollow_with1Arg() {
        Counter counter = new Counter(0);

        Step step = new CounterStep(counter) {

            @Override
            public void listSubjectsToFollow(Follower follower) {
                incrementCounter();
            }
        };

        StepDecorator decorator = new StepDecorator(step);
        decorator.listSubjectsToFollow(new Follower());

        Assertions.assertEquals(1, counter.get());
    }

    @Test
    void listSubjectsToFollow_with0Arg_nullFollower() {
        final Holder<Follower> followerHolder = new Holder<>();
        StepDecorator decorator = listSubjectsToFollow_inner(followerHolder);
        Follower follower = decorator.listSubjectsToFollow();

        Assertions.assertEquals(new HashSet<>(followerHolder.value.get()), new HashSet<>(follower.get()));
    }

    @Test
    void listSubjectsToFollow_withNoArg_notNullFollower() {
        final Holder<Follower> followerHolder = new Holder<>();
        StepDecorator decorator = listSubjectsToFollow_inner(followerHolder);
        decorator.listSubjectsToFollow();
        Follower follower = decorator.listSubjectsToFollow();

        Assertions.assertEquals(new HashSet<>(followerHolder.value.get()), new HashSet<>(follower.get()));
    }

    @Test
    void getQCapacity() {
        StepConfig stepConfig = new StepConfig();
        stepConfig.setBoundQueueCapacity(15);

        Step step = mock(Step.class);
        doAnswer(a -> stepConfig).when(step).getConfig();

        StepDecorator decorator = new StepDecorator(step);
        decorator.init(mock(Container.class), mock(Shouter.class));

        Assertions.assertEquals(15, decorator.getQCapacity());
    }

    @Test
    void getDistributionStrategy_takeFromFollower() {
        All2AllDistributionStrategy strategy = new All2AllDistributionStrategy();
        StepConfig stepConfig = new StepConfig();
        stepConfig.setDistributionStrategy(strategy);

        Step step = new Step() {

            @Override
            public void init(Container cntr, Shouter shouter) {
            }

            @Override
            public void onKill() {
            }

            @Override
            public StepConfig getConfig() {
                return stepConfig;
            }

            @Override
            public void listSubjectsToFollow(Follower follower) {
                follower.follow("subject1", strategy)
                        .follow("subject1", strategy)
                        .follow("subject6", strategy);
            }
        };
        StepDecorator decorator = new StepDecorator(step);
        Container cntrPublic = new ContainerService();
        decorator.init(cntrPublic, mock(Shouter.class));

        cntrPublic.add(new Identifiable<>(new Subject("subject1"), "subject1"));
        cntrPublic.add(new ContainerDefaultImpl(), ContainerService.STEPPING_PRIVATE_CONTAINER);
        cntrPublic.add(new Identifiable<>(new Subject("subject1"), "subject11"));
        cntrPublic.add(new Identifiable<>(new Subject("subject6"), "subject6"));

        Assertions.assertSame(strategy, decorator.getDistributionStrategy("subject1"));
    }

    @Test
    void getDistributionStrategy_takeFromStep() {
        All2AllDistributionStrategy strategy = new All2AllDistributionStrategy();
        StepConfig stepConfig = new StepConfig();
        stepConfig.setDistributionStrategy(strategy);

        Step step = mock(Step.class);
        doAnswer(a -> stepConfig).when(step).getConfig();

        StepDecorator decorator = new StepDecorator(step);
        decorator.init(mock(Container.class), mock(Shouter.class));

        Assertions.assertSame(strategy, decorator.getDistributionStrategy("subjecto"));
    }

    @Test
    void getDistributionStrategy_nullStrategy() {
        StepConfig stepConfig = new StepConfig();
        stepConfig.setDistributionStrategy(null);

        Step step = mock(Step.class);
        doAnswer(a -> stepConfig).when(step).getConfig();

        StepDecorator decorator = new StepDecorator(step);

        Assertions.assertThrows(SteppingException.class, () -> decorator.getDistributionStrategy("subjecto"));
    }

    @Test
    void getConfig_nullStepConfig() {
        Step step = mock(Step.class);
        StepDecorator decorator = new StepDecorator(step);

        Assertions.assertThrows(IdentifiableSteppingException.class, () -> decorator.init(mock(Container.class), mock(Shouter.class)));
    }

    // TODO remove after other classes, for example Subject, which use the distribution node Id provide coverage to the getter and setter method
    @Test
    void getAndSet_DistributionNodeID() {
        StepDecorator decorator = new StepDecorator(null);
        decorator.setDistributionNodeID("dddId");
        Assertions.assertEquals("dddId", decorator.getDistributionNodeID());
    }

    @Test
    void close_nullCb() {
        Counter counter = new Counter(0);
        Step step = new Step() {

            @Override
            public void init(Container cntr, Shouter shouter) {
            }

            @Override
            public void onKill() {
                counter.decrement();
            }
        };
        StepDecorator decorator = new StepDecorator(step);
        decorator.close();

        Assertions.assertEquals(-1, counter.get());
    }

    @Test
    void close_notNullCb() {
        Counter counter = new Counter(0);
        Step step = new Step() {

            @Override
            public void init(Container cntr, Shouter shouter) {
            }

            @Override
            public void onKill() {
                counter.decrement();
            }

            @Override
            public void onTickCallBack() {
                counter.decrement();
            }
        };
        StepDecorator decorator = new StepDecorator(step);
        decorator.init(mock(Container.class), mock(Shouter.class));

        CyclicBarrier cb = new CyclicBarrier(2);
        decorator.q.queue(new Message(new Data(cb), BuiltinSubjectType.STEPPING_TIMEOUT_CALLBACK.name()));
        new Thread(decorator::openDataSink).start();
        try {
            cb.await();
        } catch (InterruptedException|BrokenBarrierException ex) {
            throw new RuntimeException("Interrupted or broken while awaiting on cyclic barrier", ex);
        }
        decorator.q.queue(new Message(new Data(cb), BuiltinSubjectType.STEPPING_TIMEOUT_CALLBACK.name()));
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            throw new RuntimeException("Interrupted or while sleeping", ex);
        }
        decorator.close();

        Assertions.assertEquals(-3, counter.get());
    }

    /**
     * Creates 2 nested basic steps by way of decoration
     */
    private StepDecorator createDoubleStep() {
        Step step = new ContainerShouterStep();
        return new StepDecorator(step);
    }

    /**
     * Creates 3 nested basic steps by way of decoration
     */
    private StepDecorator createTripleStep() {
        Step step = new ContainerShouterStep();
        StepDecorator decorator1 = new StepDecorator(step);
        return new StepDecorator(decorator1);
    }

    /**
     * Creates 3 nested steps, by way of decoration, which act as a counter
     */
    private StepDecorator createCounterTripleStep(Counter counter) {
        Step step = new CounterStep(counter);
        StepDecorator decorator1 = new StepDecorator(step);
        decorator1.setId("decorator1");
        StepDecorator decorator2 = new StepDecorator(decorator1);
        decorator2.setId("decorator2");
        return decorator2;
    }

    /**
     * Creates 2 nested steps, by way of decoration, which hold the last data they received
     */
    private StepDecorator createDataHolderDoubleStep() {
        Step step = new LastDataHolderStep();
        return new StepDecorator(step);
    }

    private StepDecorator createDoubleConfigStep(StepConfig config) {
        Step step = new StepWithConfig(config);
        return new StepDecorator(step);
    }

    private Data createNonExpirableData() {
        Data data = mock(Data.class);
        doAnswer(a -> false).when(data).isExpirable();
        return data;
    }

    private Data createExpirableData(boolean canGrabAndExpire) {
        Data data = new Data("value");
        data.setExpirationCondition((data1, context) -> canGrabAndExpire, 1);
        return data;
    }

    private StepDecorator listSubjectsToFollow_inner(Holder<Follower> followerHolder) {
        Step step = new Step() {

            @Override
            public void init(Container cntr, Shouter shouter) {
            }

            @Override
            public void onKill() {
            }

            @Override
            public void listSubjectsToFollow(Follower follower) {
                followerHolder.value = follower.follow("subject1")
                        .follow("subject11")
                        .follow("subject111");
            }
        };

        return new StepDecorator(step);
    }

}