package com.imperva.stepping;

import java.util.List;

public class ContainerService implements Container {

    private final Container cntr;
    private final IStepDecorator stepDecorator;

    ContainerService(Container cntr, IStepDecorator stepDecorator){
        this.cntr = cntr;
        this.stepDecorator = stepDecorator;
    }

    public RunningScheduled getTickCallbackRunning() {
        try {
            return getById(stepDecorator.getStep().getId() + ".runningScheduled");
        } catch (Exception e) {
            throw new RuntimeException("Running object not found. Please make sure that you enabled TickCallback for this step");
        }
    }

    @Override
    public <T> ContainerDefaultImpl add(T obj) {
        return cntr.add(obj);
    }

    @Override
    public <T> ContainerDefaultImpl add(T obj, String id) {
        return cntr.add(obj, id);
    }

    @Override
    public <T> ContainerDefaultImpl add(Identifiable<T> identifiable) {
        return cntr.add(identifiable);
    }

    @Override
    public ContainerDefaultImpl remove(String id) {
        return cntr.remove(id);
    }

    @Override
    public ContainerDefaultImpl add(List<Identifiable> identifiables) {
        return cntr.add(identifiables);
    }

    @Override
    public <T> T getById(String id) {
        return cntr.getById(id);
    }

    @Override
    public <T> List<T> getSonOf(Class<?> interf) {
        return cntr.getSonOf(interf);
    }

    @Override
    public <T> List<T> getTypeOf(Class<?> interf) {
        return cntr.getTypeOf(interf);
    }

    @Override
    public void clear() {
         cntr.clear();
    }

    @Override
    public int size() {
        return cntr.size();
    }

    @Override
    public boolean exist(String name) {
        return cntr.exist(name);
    }
}
