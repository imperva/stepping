package Stepping;

import java.util.List;

public interface ISubject {
   String getType();
   void setType(String type);
   void setData(Data data);
   Data getData();
   List<IStepDecorator> getSubscribers();
   void attach(IStepDecorator o);
   void publish();
   Container getContainer() ;
   void setContainer(Container container);
}
