package com.imperva.stepping;

public class SteppingSystemException extends RuntimeException {


    SteppingSystemException(Exception e) {
        super(e);
    }

    SteppingSystemException(String message, Exception e) {
        super(message, e);
    }
}
