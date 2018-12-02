package stepping;

public class Shouter {
    private SubjectContainer subjectContainer;

    public Shouter(SubjectContainer subjectContainer) {
        this.subjectContainer = subjectContainer;
    }

    public void shout(String subjectType, Object value) {
        subjectContainer.getByName(subjectType).publish(value);
    }

    public void shout(String subjectType, Data value) {
        subjectContainer.getByName(subjectType).publish(value);
    }
}
