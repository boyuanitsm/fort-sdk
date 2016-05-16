package com.boyuanitsm.fortsdk.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A SecurityRole.
 */
public class SecurityRole extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String description;

    private String st;

    private SecurityApp app;

    private Set<SecurityAuthority> authorities = new HashSet<SecurityAuthority>();

    private Set<SecurityUser> users = new HashSet<SecurityUser>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Set<SecurityAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<SecurityAuthority> securityAuthorities) {
        this.authorities = securityAuthorities;
    }

    public Set<SecurityUser> getUsers() {
        return users;
    }

    public void setUsers(Set<SecurityUser> securityUsers) {
        this.users = securityUsers;
    }

    @Override
    public String toString() {
        return "SecurityRole{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", description='" + description + "'" +
            ", st='" + st + "'" +
            '}';
    }
}
