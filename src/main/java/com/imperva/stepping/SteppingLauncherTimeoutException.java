package com.imperva.stepping;

public class SteppingLauncherTimeoutException extends RuntimeException {
    public SteppingLauncherTimeoutException(){
        super();
    }

    public SteppingLauncherTimeoutException(String error){
        super(error);
    }
}
