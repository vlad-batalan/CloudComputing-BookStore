package org.tuiasi.cc.exceptions;

public class CannotDeserializeException extends RuntimeException{
    public CannotDeserializeException(String message) {
        super(message);
    }

    public CannotDeserializeException(String message, Throwable cause) {
        super(message, cause);
    }
}
