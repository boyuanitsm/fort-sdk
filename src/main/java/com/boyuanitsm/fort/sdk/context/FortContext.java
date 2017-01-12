/*
 * Copyright 2016-2017 Shanghai Boyuan IT Services Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
