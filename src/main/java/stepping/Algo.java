package stepping;

import java.io.Closeable;

public interface Algo extends Closeable {

    void init();

    ContainerRegistrar containerRegistration();

    void onTickCallBack();

    AlgoConfig getConfig();
}