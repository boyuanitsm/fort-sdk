package com.boyuanitsm.fort.sdk.domain;

import com.boyuanitsm.fort.sdk.domain.enumeration.ResourceEntityType;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A SecurityResourceEntity.
 */
public class SecurityResourceEntity extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String url;

    private String description;

    private ResourceEntityType resourceType;

    private String st;

    private SecurityApp app;

    private Set<SecurityAuthority> authorities = new HashSet<SecurityAuthority>();

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ResourceEntityType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceEntityType resourceType) {
        this.resourceType = resourceType;
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

    @Override
    public String toString() {
        return "SecurityResourceEntity{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", url='" + url + "'" +
            ", description='" + description + "'" +
            ", resourceType='" + resourceType + "'" +
            ", st='" + st + "'" +
            '}';
    }
}
