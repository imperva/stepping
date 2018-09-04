package Stepping;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by gabi.beyo on 1/31/2018.
 */
class Q<T>{
    private BlockingQueue<T> blockingQueue = new LinkedBlockingDeque<T>();


    public Q() {

    }

    public Q(String config) {

    }

    public List peek() {
        blockingQueue.peek();
        return null;
    }

    public void queue(T incident) {
        blockingQueue.add(incident);

    }

    public void queue(List<T> incidents) {
        blockingQueue.addAll(incidents);
    }

    public boolean contains() {
        return !blockingQueue.isEmpty();
    }

    public List<T> take() {
        List<T> incidents = new ArrayList<>();
        blockingQueue.drainTo(incidents);
        return incidents;
    }

    public List<T> take(int max) {
        List<T> incidents = new ArrayList<>();
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
