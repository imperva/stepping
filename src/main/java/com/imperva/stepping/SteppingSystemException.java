package com.imperva.stepping;

public class SteppingSystemException extends RuntimeException {

    SteppingSystemException(String message) {
        super(message);
    }

    SteppingSystemException(Exception e) {
        super(e);
    }

    SteppingSystemException(String message, Exception e) {
        super(message, e);
    }
}
