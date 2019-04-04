package com.imperva.stepping;

public interface ISubject {
   String getType();
   Data getData();
   void attach(IStepDecorator o);
   void publish(Data data);
   void publish(Object message);
}
