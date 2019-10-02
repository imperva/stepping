package com.imperva.stepping;

public interface IIdentity {
    default String getId() {
        return getClass().getName();
    }

    default void setId(String id){}
}
