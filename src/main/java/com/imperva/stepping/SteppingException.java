package com.imperva.stepping;

public class SteppingException extends RuntimeException {

    SteppingException(String message) {
        super(message);
    }

    SteppingException(Exception e) {
        super(e);
    }

    SteppingException(String message, Exception e) {
        super(message, e);
    }
}
