package com.imperva.stepping;

import java.util.List;

public interface Container {

    <T> ContainerDefaultImpl add(T obj);

    <T> ContainerDefaultImpl add(T obj, String id);

    <T> ContainerDefaultImpl add(Identifiable<T> identifiable);

    ContainerDefaultImpl remove(String id);

    ContainerDefaultImpl add(List<Identifiable> identifiables);

    <T> T getById(String id);


    <T> List<T> getSonOf(Class<?> interf);

    <T> List<T> getTypeOf(Class<?> interf);

    void clear();

    int size();

    boolean exist(String name);
}

