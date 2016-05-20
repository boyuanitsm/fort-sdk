package com.boyuanitsm.fort.sdk.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A SecurityAuthority.
 */
public class SecurityAuthority implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String description;

    private String st;

    private Set<SecurityResourceEntity> resources = new HashSet<SecurityResourceEntity>();

    private Set<SecurityRole> roles = new HashSet<SecurityRole>();

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

    public Set<SecurityResourceEntity> getResources() {
        return resources;
    }

    public void setResources(Set<SecurityResourceEntity> securityResourceEntities) {
        this.resources = securityResourceEntities;
    }

    public Set<SecurityRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<SecurityRole> securityRoles) {
        this.roles = securityRoles;
    }

    @Override
    public String toString() {
        return "SecurityAuthority{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", st='" + st + '\'' +
                ", resources=" + resources +
                ", roles=" + roles +
                '}';
    }
}
