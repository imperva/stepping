package stepping;


import java.util.List;

/**
 * Created by gabi.beyo on 12/13/2017.
 */
public class Data {
    private final boolean sync;
    private final Object value;
    private final int size;


    public Data(Object value, boolean sync) {
        this.value = value;
        this.sync = sync;

        if (value != null) {
            if (value instanceof List)
                size = ((List) value).size();
            else
                size = 1;
        } else {
            size = 0;
        }
    }

    public Data(Object value) {
       this(value, false);
    }

    public Object getValue() {
        return value;
    }


    public int getSize() {
        return size;
    }

    public boolean isAsync() {
        return sync;
    }
}
