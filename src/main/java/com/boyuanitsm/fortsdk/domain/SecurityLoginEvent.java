package com.boyuanitsm.fortsdk.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * A SecurityLoginEvent.
 */
public class SecurityLoginEvent extends AbstractAuditingEntity implements Serializable {

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
