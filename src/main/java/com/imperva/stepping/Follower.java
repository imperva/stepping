package com.imperva.stepping;

import java.util.ArrayList;
import java.util.List;

public class Follower {
    private List<FollowRequest> toFollow = new ArrayList<>();

    public Follower(){}

    public Follower(Follower follower){
       toFollow = follower.get();
    }

    public Follower follow(String subjectType) {
        toFollow.add(new FollowRequest(subjectType));
        return this;
    }

    public Follower follow(String subjectType, IDistributionStrategy distributionStrategy) {
        toFollow.add(new FollowRequest(subjectType, distributionStrategy));
        return this;
    }

    public int size() {
        return toFollow.size();
    }

    public List<FollowRequest> get(){
        return new ArrayList<>(toFollow);
    }
}
