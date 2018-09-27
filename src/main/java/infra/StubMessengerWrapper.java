package infra;

import Stepping.Data;
import Stepping.Algo;
import Stepping.IMessenger;

import java.io.IOException;

public class StubMessengerWrapper implements IMessenger {

    private Algo iAlgo;

    public StubMessengerWrapper(Algo iAlgo) {
        this.iAlgo = iAlgo;
    }

    @Override
    public void emit(Data data) {

        System.out.println("StubMessengerWrapper: Data published to Kafka");
    }

    @Override
    public Data fetching() {
        return null;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void close() throws IOException {

    }
}
