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

package com.boyuanitsm.fort.sdk.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * A SecurityLoginEvent.
 */
public class SecurityLoginEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String tokenValue;

    private Date tokenOverdueTime;

    private String ipAddress;

    private String userAgent;

    private SecurityUser user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public Date getTokenOverdueTime() {
        return tokenOverdueTime;
    }

    public void setTokenOverdueTime(Date tokenOverdueTime) {
        this.tokenOverdueTime = tokenOverdueTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public SecurityUser getUser() {
        return user;
    }

    public void setUser(SecurityUser securityUser) {
        this.user = securityUser;
    }

    @Override
    public String toString() {
        return "SecurityLoginEvent{" +
            "id=" + id +
            ", tokenValue='" + tokenValue + "'" +
            ", tokenOverdueTime='" + tokenOverdueTime + "'" +
            ", ipAddress='" + ipAddress + "'" +
            ", userAgent='" + userAgent + "'" +
            '}';
    }
}
