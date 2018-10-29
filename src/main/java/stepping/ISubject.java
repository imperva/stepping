package stepping;

public interface ISubject {
   String getType();
   Data getData();
   void attach(IStepDecorator o);
   void publish(Data data);
   Container getContainer() ;
   void setContainer(Container container);
}
