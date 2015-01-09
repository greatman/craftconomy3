package com.greatmancode.craftconomy3.utils;

public class BackendErrorException extends RuntimeException {

    public BackendErrorException(String error) {
        super(error);
    }
}
