package Stepping;

import java.util.ArrayList;
import java.util.List;

public class SubjectContainer {
    private List<ISubject<?>> subjectsList = new ArrayList<ISubject<?>>();


    public List<ISubject<?>> getSubjectsList() {
        return subjectsList;
    }

    public void setSubjectsList(List<ISubject<?>> subjectsList) {
        this.subjectsList = subjectsList;
    }

    public ISubject getByName(String name){
        for (ISubject subject: subjectsList) {
            if (subject.getType() == name)
                return subject;

        }
        return null;
    }

    public void add(ISubject subject) {
        subjectsList.add(subject);

    }
}
