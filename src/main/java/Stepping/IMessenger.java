package Stepping;

import java.io.Closeable;

public interface IMessenger extends Closeable {
    void emit(Data data);
    Data fetching();
    void shutdown();
}
