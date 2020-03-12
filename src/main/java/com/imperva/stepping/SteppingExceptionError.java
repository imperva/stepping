package com.imperva.stepping;

class SteppingExceptionError extends RuntimeException {



    SteppingExceptionError(String message) {
        super(message);
    }

    SteppingExceptionError(Error e) {
        super(e);
    }

    SteppingExceptionError(String message, Error e) {
        super(message, e);
    }
}
