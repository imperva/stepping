package stepping;

import java.io.Closeable;
import java.util.HashMap;

public interface Algo extends Closeable {

    void init();

    ContainerRegistrar containerRegistration();

    void tickCallBack();

    GlobalAlgoStepConfig getGlobalAlgoStepConfig();
}