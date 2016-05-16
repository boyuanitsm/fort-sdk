package com.boyuanitsm.fortsdk.domain;

import java.io.Serializable;

/**
 * A SecurityApp.
 */
public class SecurityApp extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String appName;

    private String appKey;

    private String appSecret;

    private String st;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    @Override
    public String toString() {
        return "SecurityApp{" +
            "id=" + id +
            ", appName='" + appName + "'" +
            ", appKey='" + appKey + "'" +
            ", appSecret='" + appSecret + "'" +
            ", st='" + st + "'" +
            '}';
    }
}
