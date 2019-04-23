package com.imperva.stepping;

import java.util.ArrayList;
import java.util.List;

public class Follower {
    private List<String> toFollow = new ArrayList<>();

    public Follower(){}

    public Follower(Follower follower){
       toFollow = follower.get();
    }

    public Follower follow(String subjectType) {
        toFollow.add(subjectType);
        return this;
    }

    public int size() {
        return toFollow.size();
    }

    public List<String> get(){
        return new ArrayList<>(toFollow);
    }
}
