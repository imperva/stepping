package com.imperva.stepping;


import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by gabi.beyo on 12/13/2017.
 */
public class Data {
    private final Object value;
    private final int size;
    private HashMap<String, Object> metaData = new HashMap<String,Object>();

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

//    public CyclicBarrier getSyncronyzer() {
//        return syncronyzer;
//    }
//
//    public void setSyncronyzer(CyclicBarrier syncronyzer) {
//        this.syncronyzer = syncronyzer;
//    }

    public void addMetadata(String key, Object val) {
        metaData.put(key,val);
    }

    public Object getMetadata(String key) {
        return metaData.get(key);
    }
}
