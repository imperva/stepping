package Stepping;

public interface IStep {
    void init();
    void attach(ISubject iSubject);
    void dataArrived(ISubject iSubject, SubjectContainer subjectContainer);
    void shutdown();
}
