package com.imperva.stepping;

public class FollowRequest {
    private String subjectType;
    private IDistributionStrategy iDistributionStrategy;
    private SubjectUpdateEvent subjectUpdateEvent;

    public FollowRequest(String subjectName) {
        this.subjectType = subjectName;
    }

    public FollowRequest(String subjectType, IDistributionStrategy distributionStrategy) {
        this.subjectType = subjectType;
        this.iDistributionStrategy = distributionStrategy;
    }

    public FollowRequest(String subjectType, IDistributionStrategy distributionStrategy, SubjectUpdateEvent subjectUpdateEvent) {
        this.subjectType = subjectType;
        this.iDistributionStrategy = distributionStrategy;
        this.subjectUpdateEvent = subjectUpdateEvent;
    }

    public FollowRequest(String subjectType, SubjectUpdateEvent subjectUpdateEvent) {
        this.subjectType = subjectType;
        this.subjectUpdateEvent = subjectUpdateEvent;
    }

    public IDistributionStrategy getDistributionStrategy() {
        return iDistributionStrategy;
    }

    public void setDistributionStrategy(IDistributionStrategy iDistributionStrategy) {
        this.iDistributionStrategy = iDistributionStrategy;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public SubjectUpdateEvent getSubjectUpdateEvent() {
        return subjectUpdateEvent;
    }

    public void setSubjectUpdateEvent(SubjectUpdateEvent subjectUpdate) {
        this.subjectUpdateEvent = subjectUpdate;
    }
}
