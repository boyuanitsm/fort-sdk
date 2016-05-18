package com.boyuanitsm.fort.sdk.context;

import com.boyuanitsm.fort.sdk.domain.SecurityUser;


/**
 * Fort Context
 *
 * @author zhanghua on 5/17/16.
 */
public class FortContext {

    private FortContext() {
        securityUser = new SecurityUser();
    }

    private SecurityUser securityUser;

    public static FortContext createEmptyContext() {
        return new FortContext();
    }

    public SecurityUser getSecurityUser() {
        return securityUser;
    }

    public void setSecurityUser(SecurityUser securityUser) {
        this.securityUser = securityUser;
    }
}
