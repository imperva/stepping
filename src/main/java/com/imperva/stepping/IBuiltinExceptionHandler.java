package com.imperva.stepping;


interface IBuiltinExceptionHandler extends IExceptionHandler {
    void handle(SteppingException e);

    void handle(SteppingSystemException e);
}
