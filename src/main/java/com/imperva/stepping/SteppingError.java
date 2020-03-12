package com.imperva.stepping;

class SteppingError extends Error {

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
