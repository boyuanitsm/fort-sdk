package com.boyuanitsm.fort.sdk.context;

/**
 * Given {@link FortContext} with the current execution thread.
 *
 * @author zhanghua on 5/17/16.
 */
public class FortContextHolder {

    public static final String FORT_SESSION_NAME = "FORT_CONTENT";

    private static FortContext context;

    public static FortContext getContext() {
        return context;
    }

    public static void setContext(FortContext context) {
        FortContextHolder.context = context;
    }
}
