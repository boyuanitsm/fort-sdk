package com.boyuanitsm.fort.sdk.exception;

/**
 * Fort crud Exception.
 *
 * @author zhanghua on 5/26/16.
 */
public class FortCrudException extends Throwable {

    public FortCrudException(String message, Throwable e) {
        super(message, e);
    }

    public FortCrudException(String message) {
        super(message);
    }

    public FortCrudException(Throwable e) {
        super(e);
    }
}
