package com.imperva.stepping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 public class Shouter {
     static final Logger logger = LoggerFactory.getLogger(IRunning.class);
     private SubjectContainer subjectContainer;
     private IExceptionHandler rootExceptionHandler;

     public Shouter(SubjectContainer subjectContainer, IExceptionHandler rootExceptionHandler) {
         this.subjectContainer = subjectContainer;
         this.rootExceptionHandler = rootExceptionHandler;
     }

     public void shout(String subjectType, Object value) {
         try {
             logger.trace("Shouting update on " + subjectType);
             subjectContainer.getByName(subjectType).publish(value);
         } catch (Exception e) {
             rootExceptionHandler.handle(new SteppingDistributionException(subjectType, "Distribution FAILED", e));
         }
     }

     public void shout(String subjectType, Data value) {
         try {
             logger.trace("Shouting update on " + subjectType);
             subjectContainer.getByName(subjectType).publish(value);
         } catch (Exception e) {
             rootExceptionHandler.handle(new SteppingDistributionException(subjectType, "Distribution FAILED", e));
         }
     }
 }
