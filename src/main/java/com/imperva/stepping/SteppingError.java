package com.imperva.stepping;

public class SteppingError extends Error {

    SteppingError(String message) {
        super(message);
    }

    SteppingError(Error e) {
        super(e);
    }

    SteppingError(String message, Error e) {
        super(message, e);
    }
}
