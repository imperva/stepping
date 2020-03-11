package com.imperva.stepping;


public interface IExceptionHandler {
    boolean handle(Exception e);
    boolean handle(Error e);

}
