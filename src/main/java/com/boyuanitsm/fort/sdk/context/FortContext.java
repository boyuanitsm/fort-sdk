package com.boyuanitsm.fort.sdk.context;

import com.boyuanitsm.fort.sdk.domain.SecurityAuthority;
import com.boyuanitsm.fort.sdk.domain.SecurityUser;

import java.util.HashSet;
import java.util.Set;


/**
 * Fort Context
 *
 * @author zhanghua on 5/17/16.
 */
public class FortContext {

    private FortContext() {
        securityUser = new SecurityUser();
        authorities = new HashSet<SecurityAuthority>();
    }

    private SecurityUser securityUser;

    private Set<SecurityAuthority> authorities;

    public static FortContext createEmptyContext() {
        return new FortContext();
    }

    public SecurityUser getSecurityUser() {
        return securityUser;
    }

    public void setSecurityUser(SecurityUser securityUser) {
        this.securityUser = securityUser;
    }

    public Set<SecurityAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<SecurityAuthority> authorities) {
        this.authorities = authorities;
    }
}
