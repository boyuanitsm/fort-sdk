package com.boyuanitsm.fort.sdk.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.boyuanitsm.fort.sdk.config.FortConfiguration;
import com.boyuanitsm.fort.sdk.domain.*;
import com.boyuanitsm.fort.sdk.exception.FortAuthenticationException;
import org.apache.http.HttpException;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * A fort client. packaging fort http interface.
 *
 * @author zhanghua on 5/16/16.
 */
@Component
public class FortClient {

    private static final String API_AUTHENTICATION = "/api/authentication";

    private static final String API_SA_SECURITY_RESOURCE_ENTITIES = "/api/sa/security-resource-entities";
    private static final String API_SECURITY_NAVS = "/api/security-navs";
    private static final String API_SA_SECURITY_AUTHORITIES = "/api/sa/security-authorities";
    private static final String API_SA_SECURITY_ROLES = "/api/sa/security-roles";
    private static final String API_SECURITY_GROUPS = "/api/security-groups";
    private static final String API_SECURITY_USER_AUTHORIZATION = "/api/security-user/authorization";

    private CookieStore cookieStore;
    private FortHttpClient fortHttpClient;

    @Autowired
    public FortClient(FortConfiguration configuration, FortHttpClient fortHttpClient) {
        this.fortHttpClient = fortHttpClient;
        try {
            this.cookieStore = loginFortSecurityServer(configuration.getApp().getAppKey(), configuration.getApp().getAppSecret());
        } catch (Exception e) {
            throw new RuntimeException("Login fort server error!", e);
        }
    }

    /**
     * login fort context server. if error throw new RuntimeException
     *
     * @param appKey the spring security j_username
     * @param secret the spring security j_password
     * @throws IOException
     * @throws HttpException
     */
    private CookieStore loginFortSecurityServer(String appKey, String secret) throws IOException, HttpException {
        return fortHttpClient.loginFortSecurityServer(API_AUTHENTICATION,
                new BasicNameValuePair("j_username", appKey),
                new BasicNameValuePair("j_password", secret),
                new BasicNameValuePair("remember-me", "true"),
                new BasicNameValuePair("submit", "Login"));
    }

    /**
     * signIn access this app server
     *
     * @param login not null
     * @param password not null, raw password
     * @throws IOException
     * @throws HttpException
     */
    public SecurityUser signIn(String login, String password, String ipAddress, String userAgent) throws IOException, HttpException {
        JSONObject obj = new JSONObject();
        obj.put("login", login);
        obj.put("passwordHash", password);
        obj.put("ipAddress", ipAddress);
        obj.put("userAgent", userAgent);
        try {
            String content = fortHttpClient.postJson(API_SECURITY_USER_AUTHORIZATION, obj);
            return JSON.toJavaObject(JSON.parseObject(content), SecurityUser.class);
        }catch (RuntimeException e) {
            throw new FortAuthenticationException("login or password fail", e);
        }
    }

    /**
     * Get this app all security resource entities
     *
     * @return this app all security resource entities
     * @throws IOException
     * @throws HttpException
     */
    public List<SecurityResourceEntity> getAllResourceEntities() throws IOException, HttpException {
        String content = fortHttpClient.get(API_SA_SECURITY_RESOURCE_ENTITIES);
        return JSONArray.parseArray(content, SecurityResourceEntity.class);
    }

    /**
     * Get this app all security navs.
     *
     * @return this app all security navs.
     * @throws IOException
     * @throws HttpException
     */
    public List<SecurityNav> getAllSecurityNavs() throws IOException, HttpException {
        String content = fortHttpClient.get(API_SECURITY_NAVS);
        return JSONArray.parseArray(content, SecurityNav.class);
    }

    /**
     * Get this app all security authorities.
     *
     * @return this app all security authorities.
     * @throws IOException
     * @throws HttpException
     */
    public List<SecurityAuthority> getAllAuthorities() throws IOException, HttpException {
        String content = fortHttpClient.get(API_SA_SECURITY_AUTHORITIES);
        return JSONArray.parseArray(content, SecurityAuthority.class);
    }

    /**
     * Get this app all security roles.
     *
     * @return this app all security roles.
     * @throws IOException
     * @throws HttpException
     */
    public List<SecurityRole> getAllRoles() throws IOException, HttpException {
        String content = fortHttpClient.get(API_SA_SECURITY_ROLES);
        return JSONArray.parseArray(content, SecurityRole.class);
    }

    /**
     * Get this app all security groups.
     *
     * @return this app all security groups.
     * @throws IOException
     * @throws HttpException
     */
    public List<SecurityGroup> getAllGroups() throws IOException, HttpException {
        String content = fortHttpClient.get(API_SECURITY_GROUPS);
        return JSONArray.parseArray(content, SecurityGroup.class);
    }

    String getCookieString() {
        String cookieString = "";

        for (Cookie cookie: cookieStore.getCookies()) {
            cookieString += cookie.getName() + "=" + cookie.getValue() + ";";
        }

        return cookieString;
    }
}
