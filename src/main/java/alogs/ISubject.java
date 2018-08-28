package alogs;

import java.util.List;

public interface ISubject<T> {
   void occurred(Data<T> data);
   Data<T> getData();
   List<IStep> getSubscribers();
   void attach(IStep o);
   void publish();
}
