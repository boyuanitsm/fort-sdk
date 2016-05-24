package com.boyuanitsm.fort.sdk.exception;

import org.apache.http.HttpException;

/**
 * when response code 400, throw. fort no valid exception.
 *
 * @author zhanghua on 5/24/16.
 */
public class FortNoValidException extends HttpException {
    public FortNoValidException(String message) {
        super(message);
    }
}
