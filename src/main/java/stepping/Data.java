package stepping;


import java.util.List;

/**
 * Created by gabi.beyo on 12/13/2017.
 */
public class Data {
    //todo thread safe
    private final Object value;

    private int size;
    public Data(Object value) {
        this.value = value;
        calcSize(value);
    }

    public Object getValue() {
        return value;
    }

    private void calcSize(Object value) {
        if(value != null){
            if(value instanceof List)
                size = ((List)value).size();
            else
                size = 1;
        }
    }

    public int getSize() {
        return size;
    }
}
