package com.imperva.stepping;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author nir.usha
 * @since 13/09/2020
 */
class RunningScheduledTest {

    AtomicReference<Integer> counter = new AtomicReference<>(0);

    synchronized void incrementCounter() {
        counter.getAndSet(counter.get() + 1);
        this.notify();
    }

    @Test
    synchronized void RunningScheduled_ctor1_verifyInitDelay() throws InterruptedException {
        try (RunningScheduled runningScheduled = new RunningScheduled("test", 1000, 200,
                TimeUnit.MILLISECONDS, this::incrementCounter)) {

            runningScheduled.awake();
            this.wait(500);
            runningScheduled.stop();
        }
        assertEquals(1, counter.get());
    }

    @Test
    synchronized void RunningScheduled_ctor1_stopBeforeDelay() throws InterruptedException {
        try (RunningScheduled runningScheduled = new RunningScheduled("test", 100, 200,
                TimeUnit.MILLISECONDS, this::incrementCounter)) {

            runningScheduled.awake();
            this.wait(500);
            runningScheduled.stop();
            this.wait(200);
        }
        assertEquals(1, counter.get());
    }

    @Test
    synchronized void RunningScheduled_ctor1_verifyDelay() throws InterruptedException {
        try (RunningScheduled runningScheduled = new RunningScheduled("test", 100, 200,
                TimeUnit.MILLISECONDS, this::incrementCounter)) {

            runningScheduled.awake();
            this.wait(500);
            this.wait(200);
        }
        assertEquals(2, counter.get());
    }

    @Test
    synchronized void RunningScheduled_ctor1_stopBeforeExecute() throws InterruptedException {
        try (RunningScheduled runningScheduled = new RunningScheduled("test", 100, 400,
                TimeUnit.MILLISECONDS, this::incrementCounter)) {

            runningScheduled.awake();
            this.wait(200);
        }
        this.wait(200);
        assertEquals(0, counter.get());
    }

    @Test
    synchronized void RunningScheduled_ctor2_verifyCron() throws InterruptedException {
        //The TickCallBack function will be called every 1 second with delay until the next sec
        try (RunningScheduled runningScheduled = new RunningScheduled("test", "1/2 * * * * *", this::incrementCounter)) {

            runningScheduled.awake();
            this.wait(2000);
        }
        assertEquals(1, counter.get());
    }

    @Test
    void RunningScheduled_ctor3_missingSetDelay() {
        assertThrows(NullPointerException.class, () -> {

            try (RunningScheduled runningScheduled = new RunningScheduled("test", this::incrementCounter)) {
                runningScheduled.awake();
            }
        });
    }


    @Test
    synchronized void setDelay_1() throws InterruptedException {
        try (RunningScheduled runningScheduled = new RunningScheduled("test", 100, 100,
                TimeUnit.MILLISECONDS, this::incrementCounter)) {

            runningScheduled.awake();
            //only the fields updated, the scheduler wont apply with the changes
            runningScheduled.setDelay(2, 1, TimeUnit.SECONDS);
            this.wait(200);
        }
        assertEquals(1, counter.get());

    }

    @Test
    synchronized void changeDelay_1() throws InterruptedException {
        try (RunningScheduled runningScheduled = new RunningScheduled("test", 2, 1,
                TimeUnit.SECONDS, this::incrementCounter)) {

            Future<?> future = runningScheduled.awake();
            //the scheduler apply with the changes
            runningScheduled.changeDelay(100, 150, TimeUnit.MILLISECONDS);
            assertTrue(future.isCancelled());

            this.wait(200);
        }
        assertEquals(1, counter.get());
    }

    @Test
    synchronized void setDelay_2() throws InterruptedException {
        try (RunningScheduled runningScheduled = new RunningScheduled("test", 100, 100,
                TimeUnit.MILLISECONDS, this::incrementCounter)) {

            runningScheduled.awake();
            //only the fields updated, the scheduler wont apply with the changes
            runningScheduled.setDelay("30 * * * * *");
            this.wait(200);
        }
        assertEquals(1, counter.get());

    }

    @Test
    synchronized void changeDelay_2() throws InterruptedException {
        //The TickCallBack function will be called every Saturday at 02:32.01am
        try (RunningScheduled runningScheduled = new RunningScheduled("test", "01 32 02 7 * *", this::incrementCounter)) {

            Future<?> future = runningScheduled.awake();
            //the scheduler apply with the changes
            runningScheduled.changeDelay(100, 150, TimeUnit.MILLISECONDS);
            assertTrue(future.isCancelled());

            this.wait(200);
        }
        assertEquals(1, counter.get());
    }

    @Test
    synchronized void setDelay_3() throws InterruptedException {
        try (RunningScheduled runningScheduled = new RunningScheduled("test", 100, 100,
                TimeUnit.MILLISECONDS, this::incrementCounter)) {

            runningScheduled.awake();
            //only the fields updated, the scheduler wont apply with the changes
            runningScheduled.setDelay("01 32 02 7 * *", 20000);
            this.wait(200);
        }
        assertEquals(1, counter.get());
    }

    @Test
    synchronized void setDelay_4() throws InterruptedException {
        try (RunningScheduled runningScheduled = new RunningScheduled("test", 2, 1,
                TimeUnit.MINUTES, this::incrementCounter)) {

            //only the fields updated, the scheduler wont apply with the changes
            runningScheduled.setDelay("01 32 02 7 * *", 1, TimeUnit.SECONDS);
            runningScheduled.awake();

            this.wait(2000);
        }
        assertEquals(1, counter.get());
    }

    @Test
    void stop() {
        RunningScheduled runningScheduled = new RunningScheduled("test", 100, 100,
                TimeUnit.MILLISECONDS, this::incrementCounter);

        runningScheduled.stop();
    }

    @Test
    void stop_futureCanceled() {
        RunningScheduled runningScheduled = new RunningScheduled("test", 100, 100,
                TimeUnit.MINUTES, this::incrementCounter);
        Future<?> future = runningScheduled.awake();
        runningScheduled.stop();

        //verify the future canceled
        assertTrue(future.isCancelled());
    }

    @Test
    void close() {
        try (RunningScheduled runningScheduled = new RunningScheduled("test", 100, 500,
                TimeUnit.MILLISECONDS, this::incrementCounter)) {
        }
    }

    @Test
    void close_futureCanceled() {
        Future<?> future;
        try (RunningScheduled runningScheduled = new RunningScheduled("test", 100, 500,
                TimeUnit.MILLISECONDS, this::incrementCounter)) {
            future = runningScheduled.awake();
        }
        //verify the future canceled
        assertTrue(future.isCancelled());
    }
}