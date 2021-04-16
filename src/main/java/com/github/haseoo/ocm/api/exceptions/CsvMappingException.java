package com.github.haseoo.ocm.api.exceptions;

public class CsvMappingException extends Exception {
    public CsvMappingException(String msg) {
        super(msg);
    }

    public CsvMappingException(String msg, Exception cause) {
        super(msg);
        this.initCause(cause);
    }
}
