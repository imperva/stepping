package Stepping;

import java.io.Closeable;

public interface IStepDecorator extends Step, Runnable, Closeable {

    void newDataArrived(ISubject iSubject);

    void attach(ISubject iSubject);

    Step getStep();

    void setGlobalAlgoStepConfig(GlobalAlgoStepConfig globalAlgoStepConfig);
}
