package com.imperva.stepping;

public class FollowRequest {
    private String subjectType;
    private IDistributionStrategy iDistributionStrategy;

    public FollowRequest(String subjectName) {
        this.subjectType = subjectName;
    }

    public FollowRequest(String subjectType, IDistributionStrategy distributionStrategy) {
        this.subjectType = subjectType;
        this.iDistributionStrategy = distributionStrategy;
    }

    public IDistributionStrategy getiDistributionStrategy() {
        return iDistributionStrategy;
    }

    public void setiDistributionStrategy(IDistributionStrategy iDistributionStrategy) {
        this.iDistributionStrategy = iDistributionStrategy;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }
}
