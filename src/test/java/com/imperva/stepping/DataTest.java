package com.imperva.stepping;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Author: Linda Nasredin
 * Date: 06 May 2020
 */
class DataTest {

    @Test
    void getSize_nullValue() {
        Data data = new Data(null);
        Assert.assertEquals(0, data.getSize());
    }

    @Test
    void getSize_stringValue() {
        Data data = new Data("value");
        Assertions.assertEquals(1, data.getSize());
    }

    @Test
    void getSize_listValue() {
        Data data = new Data(Arrays.asList(1, 2, 3));
        Assertions.assertEquals(3, data.getSize());
    }

    @Test
    void getSize_listValueChange() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        Data data = new Data(list);
        Assertions.assertEquals(2, data.getSize());
        list.add(3);
        Assertions.assertEquals(2, data.getSize());
    }

    @Test
    void getValue() {
        Data data = new Data("value");
        Assertions.assertEquals("value", data.getValue());
    }

    @Test
    void isExpirable() {
        Data data = new Data("value");
        Assertions.assertFalse(data.isExpirable());
    }

    @Test
    void tryGrabAndExpire() {
        Data data = new Data("value");
        data.setExpirationCondition((data1, context) -> false, 1);
        Assert.assertTrue(data.isExpirable());
        Assertions.assertFalse(data.tryGrabAndExpire());
    }

}