package stepping;


import java.util.List;

/**
 * Created by gabi.beyo on 12/13/2017.
 */
public class Data {
    private final Object value;
    private final int size;

    public Data(Object value) {
        this.value = value;

        if(value != null){
            if(value instanceof List)
                size = ((List)value).size();
            else
                size = 1;
        }else{
            size = 0;
        }
    }

    public Object getValue() {
        return value;
    }


    public int getSize() {
        return size;
    }
}
