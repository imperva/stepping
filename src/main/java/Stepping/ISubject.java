package Stepping;

import Stepping.container.Container;

import java.util.List;

public interface ISubject<T> {
   String getType();
   void setType(String type);
   void setData(Data<T> data);
   Data<T> getData();
   List<IStep> getSubscribers();
   void attach(IStep o);
   void publish();
   Container getContainer() ;
   void setContainer(Container container);
}
