package com.docutools.visualcrossing;

public class VisualCrossingException extends RuntimeException {
    public VisualCrossingException(String message) {
        super(message);
    }

    public VisualCrossingException(String message, Throwable cause) {
        super(message, cause);
    }
}
