package com.boyuanitsm.fort.sdk.context;

import com.boyuanitsm.fort.sdk.domain.SecurityAuthority;
import com.boyuanitsm.fort.sdk.domain.SecurityUser;
import com.boyuanitsm.fort.sdk.domain.TreeSecurityNav;

import java.util.HashSet;
import java.util.List;
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

    private List<TreeSecurityNav> navs;

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

    public List<TreeSecurityNav> getNavs() {
        return navs;
    }

    public void setNavs(List<TreeSecurityNav> navs) {
        this.navs = navs;
    }
}
