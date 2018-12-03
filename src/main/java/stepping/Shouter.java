package stepping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 public class Shouter {
     static final Logger LOGGER = LoggerFactory.getLogger(IRunning.class);
     private SubjectContainer subjectContainer;

     public Shouter(SubjectContainer subjectContainer) {
         this.subjectContainer = subjectContainer;
     }

     public void shout(String subjectType, Object value) {
         subjectContainer.getByName(subjectType).publish(value);
     }

     public void shout(String subjectType, Data value) {
         subjectContainer.getByName(subjectType).publish(value);
     }
 }
