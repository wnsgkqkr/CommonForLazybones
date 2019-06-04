package com.cfl.customexception;

public class GetHttpStatusException extends RuntimeException {
    public GetHttpStatusException(Throwable cause) {
        super(cause);
    }
}
