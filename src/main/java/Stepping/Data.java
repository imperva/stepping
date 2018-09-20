package Stepping;

/**
 * Created by gabi.beyo on 12/13/2017.
 */
public class Data {

    private Object value;

    public Data(Object value) {
        setValue(value);
    }

    public Data() {
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
