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

/**
 * A SecurityNav.
 */
public class SecurityNav implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String icon;

    private String description;

    private String st;

    private SecurityNav parent;

    private SecurityResourceEntity resource;

    /**
     * SecurityNav position (sort)
     * Use base type. when position is null, default is 0.0
     */
    private double position;

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

    public double getPosition() {
        return position;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "SecurityNav{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", description='" + description + '\'' +
                ", st='" + st + '\'' +
                ", parent=" + parent +
                ", resource=" + resource +
                ", position=" + position +
                '}';
    }
}
