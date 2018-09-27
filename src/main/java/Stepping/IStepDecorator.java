package Stepping;

public interface IStepDecorator extends Step, Runnable {

    void newDataArrived(ISubject iSubject);

    void attach(ISubject iSubject);

    Step getStep();

}
