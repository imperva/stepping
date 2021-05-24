package com.imperva.stepping;

import java.util.List;

class Visualizer {
    private List<Subject> subjects;

    public Visualizer(List<Subject> subjects) {
        this.subjects = subjects;
    }

    void draw(String senderId, String subjectType){
        Subject relevantSubjects = subjects.stream().filter(x -> x.getSubjectType().equals(subjectType)).findFirst().get();
        List<String> stepsReceivers = relevantSubjects.getObservers();
        System.out.println("**** Step Id: " + senderId + " is sending Subject: " + subjectType + " to the following Steps : " + String.join(",", stepsReceivers));
    }
}
