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

package com.boyuanitsm.fort.sdk.bean;


import com.boyuanitsm.fort.sdk.bean.enumeration.OnUpdateSecurityResourceClass;
import com.boyuanitsm.fort.sdk.bean.enumeration.OnUpdateSecurityResourceOption;

/**
 * Message of the on update security resource.
 *
 * @author zhanghua on 5/23/16.
 */
public class OnUpdateSecurityResource {

    public OnUpdateSecurityResource() {
    }

    public OnUpdateSecurityResource(OnUpdateSecurityResourceOption option, OnUpdateSecurityResourceClass resourceClass, Object data) {
        this.option = option;
        this.resourceClass = resourceClass;
        this.data = data;
    }

    /**
     * Option. RESTful style.
     */
    private OnUpdateSecurityResourceOption option;

    /**
     * Update resource class.
     */
    private OnUpdateSecurityResourceClass resourceClass;

    /**
     * Data.
     */
    private Object data;

    public OnUpdateSecurityResourceOption getOption() {
        return option;
    }

    public void setOption(OnUpdateSecurityResourceOption option) {
        this.option = option;
    }

    public OnUpdateSecurityResourceClass getResourceClass() {
        return resourceClass;
    }

    public void setResourceClass(OnUpdateSecurityResourceClass resourceClass) {
        this.resourceClass = resourceClass;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "OnUpdateSecurityResource{" +
                "option=" + option +
                ", resourceClass=" + resourceClass +
                ", data=" + data +
                '}';
    }
}
