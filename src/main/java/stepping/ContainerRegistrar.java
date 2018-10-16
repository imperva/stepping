package stepping;

import java.util.HashMap;

public class ContainerRegistrar {
    private HashMap<String, Object> registered = new HashMap<>();
    public void add(String id, Object object){
        registered.put(id, object);
    }

    public HashMap<String, Object> getRegistered(){
        return registered;
    }
}
