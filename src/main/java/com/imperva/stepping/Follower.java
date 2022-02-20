package com.imperva.stepping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class Follower {
    private List<FollowRequest> toFollow = new ArrayList<>();
    private HashMap<String, FollowRequest> followRequestBySubject = new HashMap<>();

    public Follower() {
    }

    public Follower(Follower follower) {
        toFollow = follower.get();
    }

    public Follower follow(String subjectType) {
        FollowRequest followRequest = new FollowRequest(subjectType);
        toFollow.add(followRequest);
        followRequestBySubject.put(subjectType, followRequest);
        return this;
    }

    public Follower follow(String subjectType, IDistributionStrategy distributionStrategy) {
        FollowRequest followRequest = new FollowRequest(subjectType, distributionStrategy);
        toFollow.add(followRequest);
        followRequestBySubject.put(subjectType, followRequest);
        return this;
    }

    public Follower follow(String subjectType, IDistributionStrategy distributionStrategy, SubjectUpdateEvent subjectUpdate) {
        FollowRequest followRequest = new FollowRequest(subjectType, distributionStrategy, subjectUpdate);
        toFollow.add(followRequest);
        followRequestBySubject.put(subjectType, followRequest);
        return this;
    }

    public Follower follow(String subjectType, SubjectUpdateEvent subjectUpdate) {
        FollowRequest followRequest = new FollowRequest(subjectType, subjectUpdate);
        toFollow.add(followRequest);
        followRequestBySubject.put(subjectType, followRequest);
        return this;
    }

    public int size() {
        return toFollow.size();
    }

    public List<FollowRequest> get() {
        return new ArrayList<>(toFollow);
    }

    public Optional<FollowRequest> getFollowRequest(String subjectType) {
        FollowRequest followRequest = followRequestBySubject.get(subjectType);
        Optional<FollowRequest> followRequestOpt = Optional.empty();
        if (followRequest != null) {
            followRequestOpt = Optional.of(followRequest);
        }
        return followRequestOpt;
    }
}
