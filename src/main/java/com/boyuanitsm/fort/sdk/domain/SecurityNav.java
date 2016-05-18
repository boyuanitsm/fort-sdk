package com.boyuanitsm.fort.sdk.domain;

import java.io.Serializable;

/**
 * A SecurityNav.
 */
public class SecurityNav extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String icon;

    private String description;

    private String st;

    private SecurityNav parent;

    private SecurityResourceEntity resource;

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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

    public SecurityNav getParent() {
        return parent;
    }

    public void setParent(SecurityNav securityNav) {
        this.parent = securityNav;
    }

    public SecurityResourceEntity getResource() {
        return resource;
    }

    public void setResource(SecurityResourceEntity securityResourceEntity) {
        this.resource = securityResourceEntity;
    }

    @Override
    public String toString() {
        return "SecurityNav{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", icon='" + icon + "'" +
            ", description='" + description + "'" +
            ", st='" + st + "'" +
            '}';
    }
}
