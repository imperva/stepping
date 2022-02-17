package com.imperva.stepping;

import java.util.List;

class StepInfo {
     private int numOfNodes;
     private String id;
     private String distributionNodeId;
     private boolean isSystem;

     StepInfo(String id, String distributionNodeId, boolean isSystem) {
         this.id = id;
         this.distributionNodeId = distributionNodeId;
         this.isSystem = isSystem;
         this.numOfNodes = 1;
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
 }
