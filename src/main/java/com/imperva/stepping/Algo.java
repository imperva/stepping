package com.imperva.stepping;

import java.io.Closeable;

public interface Algo extends Closeable {

    void init();

    ContainerRegistrar containerRegistration();

    void onTickCallBack();

    default AlgoConfig getConfig(){
        return new AlgoConfig();
    }
}