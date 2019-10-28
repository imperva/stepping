package com.imperva.stepping;

import java.util.HashMap;

public class ContainerRegistrar {
    private HashMap<String, Object> registered = new HashMap<>();
    public void add(String id, Object object){
        if(object instanceof IIdentity) {
            throw new SteppingException("Invalid initialization: an IIdentity object should be registered by using the method 'add(IIdentity object)'");
        }
        if(StringUtils.isEmpty(id)) {
            throw new SteppingException("Invalid initialization: you tried to register an object with an empty id.") ;
        }
        registered.put(id, object);
    }

    public void add(IIdentity object) {
        if(StringUtils.isEmpty(object.getId())) {
            throw new SteppingException("Invalid initialization: you tried to register an IIdentity object with and empty id. Please override getId() and setId()");
        }
        registered.put(object.getId(), object);
    }

    public HashMap<String, Object> getRegistered(){
        return registered;
    }
}
