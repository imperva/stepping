package com.imperva.stepping;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class SubjectContainer {
    //todo improve performance (O)N + merge with global Container
    private List<ISubject> subjectsList = new CopyOnWriteArrayList<>();


    List<ISubject> getSubjectsList() {
        return subjectsList;
    }

    void setSubjectsList(List<ISubject> subjectsList) {
        this.subjectsList = subjectsList;
    }

    ISubject getByName(String name) {
        for (ISubject subject : subjectsList) {
            if (subject.getType().equals(name))
                return subject;

        }
        return null;
    }

    void add(ISubject subject) {
        subjectsList.add(subject);
    }
}
