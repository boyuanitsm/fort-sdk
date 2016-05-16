package com.boyuanitsm.fortsdk.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A SecurityUser.
 */
public class SecurityUser extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String login;

    private String passwordHash;

    private String email;

    private Boolean activated;

    private String st;

    private SecurityApp app;

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

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public SecurityApp getApp() {
        return app;
    }

    public void setApp(SecurityApp securityApp) {
        this.app = securityApp;
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

    @Override
    public String toString() {
        return "SecurityUser{" +
            "id=" + id +
            ", login='" + login + "'" +
            ", passwordHash='" + passwordHash + "'" +
            ", email='" + email + "'" +
            ", activated='" + activated + "'" +
            ", st='" + st + "'" +
            '}';
    }
}
