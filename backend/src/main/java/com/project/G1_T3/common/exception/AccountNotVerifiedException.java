package com.project.G1_T3.common.exception;

import org.springframework.security.core.AuthenticationException;

public class AccountNotVerifiedException extends AuthenticationException {
    public AccountNotVerifiedException(String msg) {
        super(msg);
    }
}