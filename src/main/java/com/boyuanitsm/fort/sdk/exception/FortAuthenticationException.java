package com.boyuanitsm.fort.sdk.exception;

/**
 * fort authentication exception. throw when authentication error.
 *
 * @author zhanghua on 5/19/16.
 */
public class FortAuthenticationException extends RuntimeException{

    public FortAuthenticationException(String message, Throwable e) {
        super(message, e);
    }
}
