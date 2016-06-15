package com.boyuanitsm.fort.sdk.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * A SecurityUser.
 */
public class SecurityUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String login;

    private String passwordHash;

    private String email;

    private Boolean activated;

    private String ipAddress;

    private String userAgent;

    private String token;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Z")
    private Date tokenOverdueTime;

    private Set<SecurityRole> roles = new HashSet<SecurityRole>();

    private Set<SecurityGroup> groups = new HashSet<SecurityGroup>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean isActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public Set<SecurityRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<SecurityRole> securityRoles) {
        this.roles = securityRoles;
    }

    public Set<SecurityGroup> getGroups() {
        return groups;
    }

    public void setGroups(Set<SecurityGroup> securityGroups) {
        this.groups = securityGroups;
    }

    public Boolean getActivated() {
        return activated;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getTokenOverdueTime() {
        return tokenOverdueTime;
    }

    public void setTokenOverdueTime(Date tokenOverdueTime) {
        this.tokenOverdueTime = tokenOverdueTime;
    }

    @Override
    public String toString() {
        return "SecurityUser{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", email='" + email + '\'' +
                ", activated=" + activated +
                ", ipAddress='" + ipAddress + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", token='" + token + '\'' +
                ", tokenOverdueTime=" + tokenOverdueTime +
                ", roles=" + roles +
                ", groups=" + groups +
                '}';
    }
}
