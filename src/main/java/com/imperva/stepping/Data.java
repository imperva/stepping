package com.imperva.stepping;


import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by gabi.beyo on 12/13/2017.
 */
public class Data {
    private final Object value;
    private final int size;

    private boolean isExpirable;
    private int expectedValue;
    private int newValue;
    private AtomicInteger atomicInteger;

    public Data(Object value) {
        this.value = value;

        if (value != null) {
            if (value instanceof List)
                size = ((List) value).size();
            else
                size = 1;
        } else {
            size = 0;
        }
    }

    public Object getValue() {
        return value;
    }


    public int getSize() {
        return size;
    }

    boolean isExpirable() {
        return isExpirable;
    }

    void setExpirationCondition(int expectedValue, int newValue) {
        this.expectedValue = expectedValue;
        this.newValue = newValue;
        this.atomicInteger = new AtomicInteger(expectedValue);
        this.isExpirable = true;
    }

    boolean tryGrabAndDeprecate() {
        return atomicInteger.compareAndSet(expectedValue, newValue);
    }
}
