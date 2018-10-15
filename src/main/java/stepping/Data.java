package stepping;


import java.util.List;

/**
 * Created by gabi.beyo on 12/13/2017.
 */
public class Data {

    private Object value;
    private String subjectType;
    private int size;

    public Data(Object value) { this(value, null);

    }

    public Data() {
    }

    public Data(Object value, String type) {
        setValue(value);
        setSubjectType(type);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
        if(value != null){
            if(value instanceof List)
                size = ((List)value).size();
            else
                size = 1;
        }
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }


    public int getSize() {
        return size;
    }
}
