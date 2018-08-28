package alogs;

public interface IStep {
    void init();
    void attach(ISubject iSubject);
    void dataArrived(Data data);
    void shutdown();
}
