package com.imperva.stepping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * @author nir.usha
 * @since 13/09/2020
 */
class IRunningTest {

    private IRunning iRunning;

    @BeforeEach
    void setUp() {
        iRunning = spy(IRunning.class);
    }

    @Test
    void close_noFuture() {
        iRunning.close(null, true);
    }

    @Test
    void close() {
        AtomicReference<Integer> counter = new AtomicReference<>(0);
        Future future = mock(Future.class);
        when(future.cancel(eq(false))).thenAnswer((a) -> counter.getAndSet(counter.get() + 1));
        iRunning.close(future, false);

        assertEquals(1, counter.get());
    }

    @Test
    void close_force() {
        AtomicReference<Integer> counter = new AtomicReference<>(0);
        Future future = mock(Future.class);
        when(future.cancel(eq(true))).thenAnswer((a) -> counter.getAndSet(counter.get() + 1));
        iRunning.close(future, true);

        assertEquals(1, counter.get());
    }
}