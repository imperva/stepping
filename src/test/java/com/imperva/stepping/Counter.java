package com.imperva.stepping;

/**
 * A simple way to pass integer by reference which is used for basic counting functionality - add 1 or subtract 1
 *
 * Why not org.apache.commons.lang.mutable.MutableInt? We don't want to add commons-lang3 dependency just for one class
 * Why not AtomicReference? It's an overkill, most of the time we don't need atomicity
 * Why not sun.java2d.xr.MutableInteger? It's a java internal class and we can't reference it from our code (all sun.* classes are)
 *
 * Author: Linda Nasredin
 * Date: 21 Sep 2020
 */
class Counter {

    private int value;

    Counter(int value) {
        this.value = value;
    }

    void increment() {
        value++;
    }

    void decrement() {
        value--;
    }

    int get() {
        return value;
    }
}
