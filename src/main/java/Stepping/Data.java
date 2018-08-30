package Stepping;

/**
 * Created by gabi.beyo on 12/13/2017.
 */
public class Data<T> {

    private T data;
    
    public Data(T data){
        setData(data);
    }
    public Data(){

    }

    public T getData(){
        return data;
    }

    public void setData(T data){
        this.data = data;
    }
}
