package com.meryem.fashionstore.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }

    public EmailAlreadyExistsException() {
        super("Bu e-posta adresi zaten kayıtlı");
    }
}