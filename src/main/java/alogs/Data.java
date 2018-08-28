package alogs;

/**
 * Created by gabi.beyo on 12/13/2017.
 */
public class Data<T> {
    private String type;
    private T data;
    
    public Data(T data){
        setData(data);
    }
    public Data(){

    }

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    public T getData(){
        return data;
    }

    public void setData(T data){
        this.data = data;
    }
}
