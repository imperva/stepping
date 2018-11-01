package stepping;

import java.security.InvalidParameterException;
import java.util.concurrent.ConcurrentHashMap;


public class Shutter {
    private volatile ConcurrentHashMap<String, DataCloneable> mocks = new ConcurrentHashMap<>();
    private volatile ConcurrentHashMap<String, DataCloneable> snapshots = new ConcurrentHashMap<>();
    private volatile ConcurrentHashMap<String, DataCloneable> temp = null;

    Shutter brandNew(){
        mocks = new ConcurrentHashMap<>();
        return this;
    }

    Shutter cp(String key, DataCloneable cloneable) {
        if (cloneable == null)
            throw new InvalidParameterException();
        try {
            mocks.put(key, cloneable.clone());
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    Shutter swap() {
        snapshots = mocks;
        mocks = new ConcurrentHashMap<>();
        return this;
    }

    ConcurrentHashMap<String, DataCloneable> get(){
        return snapshots;
    }

    DataCloneable getById(String key){
        return snapshots.get(key);
    }

    DataCloneable popById(String key){
        return snapshots.remove(key);
    }
}
