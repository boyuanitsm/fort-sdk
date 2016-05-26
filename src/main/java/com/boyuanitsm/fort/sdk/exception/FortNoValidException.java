package com.boyuanitsm.fort.sdk.exception;

/**
 * when response code 400, throw. fort no valid exception.
 *
 * @author zhanghua on 5/24/16.
 */
public class FortNoValidException extends FortCrudException {
    public FortNoValidException(String message) {
        super(message);
    }
}
