package com.imperva.stepping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ContainerDefaultImpl implements Container{
    private volatile ConcurrentHashMap<String,Identifiable> objects = new ConcurrentHashMap<>();

    public ContainerDefaultImpl() {
    }

    public <T> ContainerDefaultImpl add(T obj) {
        add(new Identifiable<T>(obj, obj.getClass().getName() + obj.hashCode()));
        return this;
    }

    public <T> ContainerDefaultImpl add(T obj, String id) {
        add(new Identifiable<T>(obj, id));
        return this;
    }

    public <T> ContainerDefaultImpl add(Identifiable<T> identifiable) {
        if (objects.containsKey(identifiable.getId()))
            throw new IdentifiableSteppingException(identifiable.getId(), "Identifiable Object must contain unique ID. " + identifiable.getId() + " already exists!");
        objects.putIfAbsent(identifiable.getId(),identifiable);
        return this;
    }

    @Override
    public <T> void add(HashMap<String, T> objs) {
        objs.forEach((s, o) -> add(o, s));
    }

    public ContainerDefaultImpl remove(String id) {
        objects.remove(id);
        return this;
    }

    public ContainerDefaultImpl add(List<Identifiable> identifiables) {
        if(identifiables == null)
            throw new SteppingException("Can't add null object to Container");
        for (Identifiable identifiable :  identifiables) {
            add(identifiable);
        }
        return this;
    }

    public <T> T getById(String id) {
        Identifiable obj = objects.get(id);
        if(obj == null)
            return null;

        return (T)obj.get();
    }

    private boolean getSonOf(Class<?> interf, Class clss, List<Class> context) {
        if (clss == null) {
            for (Class c : context) {
                if (c.equals(interf)) {
                    return true;
                }
            }
            return false;
        }

        if (clss.equals(interf)) {
            return true;
        }

        for (Class intfc : clss.getInterfaces()) {
            if (intfc.equals(interf)) {
                return true;
            }
            context.addAll(Arrays.asList(intfc.getInterfaces()));
        }
        return getSonOf(interf, clss.getSuperclass(), context);
    }

    public <T> List<T> getSonOf(Class<?> interf) {
        List<T> ts = new ArrayList<>();
        List<Object> objects2 = objects.entrySet().stream().map((obj) -> (obj.getValue()).get()).collect(Collectors.toList());
        for (Object o : objects2) {
            Boolean found = getSonOf(interf, o.getClass(), new ArrayList<>());
            if (found)
                ts.add((T) o);
        }
        return ts;
    }

    public <T> List<T> getTypeOf(Class<?> interf) {
        List<T> ts = new ArrayList<>();
        List<Object> objects2 = objects.entrySet().stream().map((obj) -> (obj.getValue()).get()).collect(Collectors.toList());
        for (Object o : objects2) {
            if (o.getClass().equals(interf))
                ts.add((T) o);
        }
        return ts;
    }

    public List<Identifiable> getAll() {
        List<Identifiable> clonedObject = objects.entrySet().stream().map((obj) -> (obj.getValue())).collect(Collectors.toList());
        return clonedObject;
    }

    public void clear() {
        if (objects != null)
            objects.clear();
    }

    public int size() {
        return objects.size();
    }

    public boolean exist(String name) {
        return objects.containsKey(name);
    }
}
