package alogs;

public interface IStep {
    void init();
    void attach(ISubject iSubject);
    void dataArrived(Data data, SubjectContainer subjectContainer);
    void shutdown();
}
