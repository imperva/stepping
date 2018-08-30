package Stepping;

import java.util.List;

public interface ISubject<T> {

   String getType();
   void setType(String type);
   void setData(Data<T> data);
   Data<T> getData();
   List<IStep> getSubscribers();
   void attach(IStep o);
   void publish();
}
