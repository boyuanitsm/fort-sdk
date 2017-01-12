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

import com.boyuanitsm.fort.sdk.domain.enumeration.ResourceEntityType;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A SecurityResourceEntity.
 */
public class SecurityResourceEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String url;

    private String description;

    private ResourceEntityType resourceType;

    private String st;

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
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                ", resourceType=" + resourceType +
                ", st='" + st + '\'' +
                ", authorities=" + authorities +
                '}';
    }
}
