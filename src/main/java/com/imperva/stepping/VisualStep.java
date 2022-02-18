package com.imperva.stepping;

import java.util.List;

class VisualStep {
     private int numOfNodes;
     private String id;
     private String distributionNodeId;
     private boolean isSystem;
    private String friendlyName;

    VisualStep(String id, String friendlyName, String distributionNodeId, int numOfNodes, boolean isSystem) {
        this.friendlyName = friendlyName;
        if(id ==null || id.isEmpty() || distributionNodeId == null || distributionNodeId.isEmpty())
             throw new SteppingException("VisualStep must be initialized with stepId and distributionNodeId");
         this.id = id;
         this.distributionNodeId = distributionNodeId;
         this.isSystem = isSystem;
         this.numOfNodes = numOfNodes;
     }

     String getId() {
         return id;
     }

     String getDistributionNodeId() {
         return distributionNodeId;
     }

     boolean isSystem() {
         return isSystem;
     }

     int getNumOfNodes() {
         return numOfNodes;
     }

     boolean hasMultipleNodes(){
         return !distributionNodeId.equals("default");
     }

    public String getFriendlyName() {
        return friendlyName;
    }
}
