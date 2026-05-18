package com.dmasone.identity.sharedkernel.domain;

/**
 * Base exception for business rule failures that should be translated into a
 * structured API error response. The code is stable enough for clients and
 * tests, while the message remains human-readable.
 */
public abstract class DomainException extends RuntimeException {

    private final String code;

    protected DomainException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String code() {
        return code;
    }
}
