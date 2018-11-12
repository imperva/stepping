package stepping;

import java.security.InvalidParameterException;
import java.util.concurrent.ConcurrentHashMap;


public class Shutter {
    private volatile ConcurrentHashMap<String, DataCloneable> snapshots = new ConcurrentHashMap<>();

    Shutter add(String key, DataCloneable cloneable) {
        if (cloneable == null)
            throw new InvalidParameterException();
        snapshots.put(key, cloneable);
        return this;
    }

    DataCloneable getById(String key) throws CloneNotSupportedException {
        return snapshots.get(key).clone();
    }

    DataCloneable popById(String key) throws CloneNotSupportedException {
        return snapshots.remove(key).clone();
    }
}
