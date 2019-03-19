package stepping;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

class SubjectContainer {
    //todo improve performance (O)N + merge with global Container
    private ConcurrentHashMap<String, ISubject> subjectsList = new ConcurrentHashMap<>();


    List<ISubject> getSubjectsList() {
        List<ISubject> subjects = new ArrayList<>(subjectsList.values());
        return subjects;
    }

    //void setSubjectsList(List<ISubject> subjectsList) {
    //    this.subjectsList = subjectsList;
    //}

    ISubject getByName(String name) {
        return subjectsList.get(name);
//        for (ISubject subject : subjectsList) {
//            if (subject.getType().equals(name))
//                return subject;
//
//        }
        //       return null;
    }

    void add(ISubject subject) {
        subjectsList.putIfAbsent(subject.getType(), subject);
    }
}
