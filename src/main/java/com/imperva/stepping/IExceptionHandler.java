package com.imperva.stepping;


public interface IExceptionHandler {
    boolean handle(Exception e);
    default boolean handle(Error e){
        throw new SteppingSystemCriticalException("Error " + e.toString() + " not handled. Default Error handler in action, throwing SteppingSystemCriticalException()");
    }

}
