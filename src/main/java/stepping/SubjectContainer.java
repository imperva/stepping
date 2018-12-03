package stepping;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SubjectContainer {
    //todo improve performance (O)N + merge with global Container
    private List<ISubject> subjectsList = new CopyOnWriteArrayList<>();


    public List<ISubject> getSubjectsList() {
        return subjectsList;
    }

    public void setSubjectsList(List<ISubject> subjectsList) {
        this.subjectsList = subjectsList;
    }

    public ISubject getByName(String name){
        for (ISubject subject: subjectsList) {
            if (subject.getType().equals(name))
                return subject;

        }
        return null;
    }

    public void add(ISubject subject) {
        subjectsList.add(subject);
    }
}
