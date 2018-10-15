package stepping;

import java.io.Closeable;
import java.util.HashMap;

public interface Algo extends Closeable {

    void init();

    HashMap<String, Object> IoC();

    void tickCallBack();

    GlobalAlgoStepConfig getGlobalAlgoStepConfig();
}