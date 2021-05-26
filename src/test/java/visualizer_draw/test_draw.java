package visualizer_draw;

import com.imperva.stepping.Stepping;

import org.junit.Ignore;
import org.junit.Test;

public class test_draw {

    @Test
    public void test1() throws InterruptedException {
        KafkaDBMergerAlgo algo = new KafkaDBMergerAlgo();
        new Stepping().register(algo).go();
        Thread.currentThread().join(3000000);
    }
}