package com.imperva.stepping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RemoteControllers {

    private HashMap<String, RemoteController> remoteControllers = new HashMap<>();

    void add(String id, RemoteController remoteController) {
        remoteControllers.put(id, remoteController);
    }

    public RemoteController get(String id) {
        if(!remoteControllers.containsKey(id))
            throw  new SteppingException("Controller id: " + id + " doesn't exists");
       return remoteControllers.get(id);
    }

}
