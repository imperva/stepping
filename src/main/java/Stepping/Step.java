package Stepping;

import java.io.Closeable;

public interface  Step extends Closeable {

      void init();

      boolean isAttach(String eventType);

      void newDataArrivedCallBack(ISubject subject, SubjectContainer subjectContainer);

      void tickCallBack();

      void restate();

      void setContainer(Container cntr);
}
