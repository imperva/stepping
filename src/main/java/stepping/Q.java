package stepping;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by gabi.beyo on 1/31/2018.
 */
public class Q<T> {

    //todo use ConcurrentLinkedQueue ??
    private BlockingQueue<T> blockingQueue = new LinkedBlockingDeque<>();

    public T peek() {
        return blockingQueue.peek();
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

    public T take() throws InterruptedException {
        T data = blockingQueue.take();
        return data;
    }


    public int size() {
        return blockingQueue.size();
    }

    public void clear() {
        blockingQueue.clear();
    }
}

