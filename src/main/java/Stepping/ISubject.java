package Stepping;

import java.util.List;

public interface ISubject {
   String getType();
   void setType(String type);
   void setData(Data data);
   Data getData();
   List<IStep> getSubscribers();
   void attach(IStep o);
   void publish();
   Container getContainer() ;
   void setContainer(Container container);
}
