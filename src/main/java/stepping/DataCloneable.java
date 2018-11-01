package stepping;

 public abstract class DataCloneable extends Data implements Cloneable {
     public DataCloneable(Object value) {
         super(value);
     }

     abstract public DataCloneable clone() throws CloneNotSupportedException;
 }
