package Stepping;

/**
 * Created by gabi.beyo on 12/13/2017.
 */
public class Data<T> {

    private T value;

    public Data(T value) {
        setValue(value);
    }

    public Data() {
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
