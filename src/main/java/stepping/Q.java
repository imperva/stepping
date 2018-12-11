package stepping;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by gabi.beyo on 1/31/2018.
 */
class Q<T> {

    private BlockingQueue<T> blockingQueue = new LinkedBlockingDeque<>();

    T peek() {
        return blockingQueue.peek();
    }

    void queue(T item) {
        blockingQueue.add(item);

    }

    void queue(List<T> items) {
        blockingQueue.addAll(items);
    }

    boolean contains() {
        return !blockingQueue.isEmpty();
    }

    T take() throws InterruptedException {
        T data = blockingQueue.take();
        return data;
    }


    int size() {
        return blockingQueue.size();
    }

    void clear() {
        blockingQueue.clear();
    }
}

