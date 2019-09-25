package com.imperva.stepping;

public interface IIdentity {
    default String getId() {
        return null;
    }

    default void setId(String id){}
}
