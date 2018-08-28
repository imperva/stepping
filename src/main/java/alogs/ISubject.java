package alogs;

import alogs.container.Identifiable;

import java.util.List;

public interface ISubject<T> {
   void occurred(Data<T> data);
   Data<T> getData();
   List<Identifiable> getSubscribers();
   void attach(Object o);
   void publish();
}
