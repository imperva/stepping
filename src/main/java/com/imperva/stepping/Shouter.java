package com.imperva.stepping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 public class Shouter {
     private final Logger logger = LoggerFactory.getLogger(IRunning.class);
     private Container container;
     private IExceptionHandler rootExceptionHandler;
     private String senderId;

     public Shouter(String senderId, Container container, IExceptionHandler rootExceptionHandler) {
         this.container = container;
         this.rootExceptionHandler = rootExceptionHandler;
         this.senderId = senderId;
     }

     public void shout(String subjectType, Object value) {
         Data data = new Data(value);
         data.setSenderId(senderId);
         shout(subjectType, data);
     }

     public void shout(String subjectType, Data value) {
         try {
             Subject subjectToNotify = handleMissingSubject(subjectType);
             if (subjectToNotify == null) return;

             value.setSenderId(senderId);
             subjectToNotify.publish(value);
         } catch (Exception e) {
             logger.error("Shouter Failed", e);
             rootExceptionHandler.handle(new SteppingDistributionException(subjectType, "Distribution FAILED", e));
         }
     }

     private Subject handleMissingSubject(String subjectType) {
         Subject subjectToNotify = container.getById(subjectType);
         if (subjectToNotify == null) {
             logger.error("Subject '" + subjectType + "' is missing, can't shout! Make sure that there is at least 1 follower attached to it");
             return null;
         }
         return subjectToNotify;
     }

     public String getSenderId() {
         return senderId;
     }
 }
