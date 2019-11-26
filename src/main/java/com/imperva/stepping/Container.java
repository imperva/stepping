package com.imperva.stepping;

import java.util.HashMap;
import java.util.List;

public interface Container {

    <T> Container add(T obj);

    <T> Container add(T obj, String id);

    <T> Container add(Identifiable<T> identifiable);

    <T> void add(HashMap<String, T> objs);

    Container remove(String id);

    Container add(List<Identifiable> identifiables);

    <T> T getById(String id);

    List<Identifiable> getAll();

    <T> List<T> getSonOf(Class<?> interf);

    <T> List<T> getTypeOf(Class<?> interf);

    void clear();

    int size();

    boolean exist(String name);
}

