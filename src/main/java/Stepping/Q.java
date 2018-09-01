package Stepping;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by gabi.beyo on 1/31/2018.
 */
class Q {
    private BlockingQueue<Data<?>> blockingQueue = new LinkedBlockingDeque<Data<?>>();


    public Q() {

    }

    public Q(String config) {

    }

    public List peek() {
        blockingQueue.peek();
        return null;
    }

    public void queue(Data<?> incident) {
        blockingQueue.add(incident);

    }

    public void queue(List<Data<?>> incidents) {
        blockingQueue.addAll(incidents);
    }

    public boolean contains() {
        return !blockingQueue.isEmpty();
    }

    public List<Data<?>> take() {
        List<Data<?>> incidents = new ArrayList<>();
        blockingQueue.drainTo(incidents);
        return incidents;
    }

    public List<Data<?>> take(int max) {
        List<Data<?>> incidents = new ArrayList<>();
        blockingQueue.drainTo(incidents, max);
        return incidents;
    }

    public int size() {
        return blockingQueue.size();
    }


    public void clear(){
        blockingQueue.clear();
    }
}
