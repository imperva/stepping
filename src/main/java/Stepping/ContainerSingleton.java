package Stepping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ContainerSingleton {
    private static ContainerSingleton instance;
    private List<Identifiable> objects = new ArrayList<>();
    private static Object lock = new Object();
    private ContainerSingleton() {
    }

    public static ContainerSingleton getInstance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new ContainerSingleton();
            }
        }
        return instance;
    }

    public <T> ContainerSingleton add(T obj) {
        add(new Identifiable<>(obj, obj.getClass().getName()));
        return this;
    }

    public <T> ContainerSingleton add(T obj, String id) {
        add(new Identifiable<>(obj, id));
        return this;
    }

    public <T> ContainerSingleton add(Identifiable<T> identifiable) {
        if (instance.objects.contains(identifiable))
            throw new RuntimeException("Identifiable Object must contain unique ID. " + identifiable.getId() + " already exists!");
        instance.objects.add(identifiable);
        return this;
    }

    public ContainerSingleton remove(String id) {
        instance.objects.removeIf((i) -> i.getId().toLowerCase().equals(id.toLowerCase()));
        return this;
    }

    public ContainerSingleton add(List<Identifiable> identifiables) {
        instance.objects.addAll(identifiables);
        return this;
    }

    public <T> T getById(String id) {
        for (Identifiable identifiable :
                instance.objects) {
            if (identifiable.getId().toLowerCase().equals(id.toLowerCase()))
                return (T) identifiable.get();
        }
        return null;
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
        List<Object> objects = instance.objects.stream().map((obj) -> ((Identifiable) obj).get()).collect(Collectors.toList());
        for (Object o : objects) {
            Boolean found = getSonOf(interf, o.getClass(), new ArrayList<>());
            if (found)
                ts.add((T) o);
        }
        return ts;
    }

    public <T> List<T> getTypeOf(Class<?> interf) {
        List<T> ts = new ArrayList<>();
        List<Object> objects = instance.objects.stream().map((obj) -> ((Identifiable) obj).get()).collect(Collectors.toList());
        for (Object o : objects) {
            if (o.getClass().equals(interf))
                ts.add((T) o);
        }
        return ts;
    }

    public static void clear() {
        if (instance != null)
            instance.objects.clear();
    }

    public int size() {
        return instance.objects.size();
    }
}
