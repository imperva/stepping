package stepping;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class DataSync extends Data {
    private final int barriers;
    private final CyclicBarrier cyclicBarrier;
    public DataSync(Object value, int barriers) {
        super(value);
        this.barriers = barriers;
        this.cyclicBarrier = new CyclicBarrier(barriers);
    }

    public DataSync(Data data, int barriers) {
        this(data.getValue(), barriers);
    }

    public void waiting() throws BrokenBarrierException, InterruptedException {
        cyclicBarrier.await();
    }
}
