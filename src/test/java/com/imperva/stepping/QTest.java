package com.imperva.stepping;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Author: Linda Nasredin
 * Date: 06 May 2020
 */
class QTest {

    @Test
    void q_zeroCapacityIsMaxCapacity() {
        Q<Integer> q = new Q<>();
        // TODO actual capacity of the inner queue is Integer.MAX_VALUE
        Assert.assertEquals(0, q.getCapacity());
    }

    @Test
    void q_positiveCapacity() {
        Q<Integer> q = new Q<>(10);
        Assert.assertEquals(10, q.getCapacity());
    }

    @Test
    void q_negativeCapacity() {
        Assertions.assertThrows(SteppingException.class, () -> new Q<>(-1));
    }

    @Test
    void peek_found() {
        Q<String> q = new Q<>(2);
        q.queue("item1");
        q.queue("item2");
        Assertions.assertEquals("item1", q.peek());
    }

    @Test
    void peek_empty() {
        Q<String> q = new Q<>(2);
        Assertions.assertNull(q.peek());
    }

    @Test
    void queue_wait_take() throws InterruptedException {
        Q<String> q = new Q<>(2);
        q.queue("item1");

        Runnable runnable = () -> {
            q.queue("item2");
            q.queue("item3");
        };
        new Thread(runnable).start();

        Thread.sleep(50);
        Assertions.assertEquals(2, q.size());
        q.take();
        Thread.sleep(50);
        Assertions.assertEquals(2, q.size());
    }

    @Test
    void contains() {
        Q<String> q = new Q<>(2);
        q.queue("item1");
        q.queue("item2");
        Assertions.assertTrue(q.contains());
    }

    @Test
    void take_wait() throws InterruptedException {
        Q<String> q = new Q<>(2);
        Runnable runnable = () -> {
            try {
                q.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        new Thread(runnable).start();

        Thread.sleep(50);
        q.queue("item1");
        Assertions.assertEquals(1, q.size());
        Thread.sleep(50);
        Assertions.assertEquals(0, q.size());
    }

    @Test
    void clear() {
        Q<String> q = new Q<>(2);
        q.queue("item1");
        q.queue("item2");
        q.clear();
        Assertions.assertEquals(0, q.size());
    }

    @Test
    void offer() {
        Q<String> q = new Q<>(1);
        boolean offered1 = q.offer("item1");
        Assertions.assertTrue(offered1);
        boolean offered2 = q.offer("item2");
        Assertions.assertFalse(offered2);
    }

}