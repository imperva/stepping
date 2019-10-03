package com.imperva.stepping;

public interface IIdentity {
    default String getId() {
        return getClass().getName() + hashCode();
    }

    default void setId(String id){}
}
