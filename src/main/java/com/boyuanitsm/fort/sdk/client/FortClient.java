package com.boyuanitsm.fort.sdk.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.boyuanitsm.fort.sdk.config.FortConfiguration;
import com.boyuanitsm.fort.sdk.domain.*;
import org.apache.http.HttpException;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger log = LoggerFactory.getLogger(FortClient.class);

    private static final String API_AUTHENTICATION = "/api/authentication";

    private static final String API_SECURITY_RESOURCE_ENTITIES = "/api/security-resource-entities";
    private static final String API_SECURITY_NAVS = "/api/security-navs";
    private static final String API_SECURITY_AUTHORITIES = "/api/security-authorities";
    private static final String API_SECURITY_ROLES = "/api/security-roles";
    private static final String API_SECURITY_GROUPS = "/api/security-groups";
    private static final String API_SECURITY_USERS = "/api/security-users";
    private static final String API_SECURITY_USER_AUTHORIZATION = "/api/security-user/authorization";

    private FortConfiguration configuration;
    private HttpClient httpClient;

    @Autowired
    public FortClient(FortConfiguration configuration) {
        this.configuration = configuration;
        httpClient = new HttpClient(configuration.getApp().getServerBase());
        try {
            loginFortSecurityServer(configuration.getApp().getAppKey(), configuration.getApp().getAppSecret());
        } catch (Exception e) {
            throw new RuntimeException("login fort context server error!", e);
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
    private void loginFortSecurityServer(String appKey, String secret) throws IOException, HttpException {
        httpClient.postForm(API_AUTHENTICATION,
                new BasicNameValuePair("j_username", appKey),
                new BasicNameValuePair("j_password", secret),
                new BasicNameValuePair("submit", "Login"));
    }

    /**
     * authorization access this app server
     *
     * @param login not null
     * @param password not null, raw password
     * @throws IOException
     * @throws HttpException
     */
    public SecurityUser authorization(String login, String password) throws IOException, HttpException {
        JSONObject obj = new JSONObject();
        obj.put("login", login);
        obj.put("passwordHash", password);
        String content = httpClient.postJson(API_SECURITY_USER_AUTHORIZATION, obj);
        return JSON.toJavaObject(JSON.parseObject(content), SecurityUser.class);
    }

    /**
     * Register a new user, before register, set default role,group.
     * role is fort.yml user: defaultRole. multi value comma split.
     * group is fort.yml user: defaultGroup. multi value comma split.
     *
     * @param user security user, login、passwordHash required
     * @throws IOException
     * @throws HttpException
     */
    public void registerUser(SecurityUser user) throws IOException, HttpException {
        // set default role, group
        user.setRoles(configuration.getUser().getDefaultRoles());
        user.setGroups(configuration.getUser().getDefaultGroups());

        httpClient.postJson(API_SECURITY_USERS, user);
    }

    /**
     * Update a already existing user. login updatable is false.
     *
     * @param user id not null
     */
    public void updateUser(SecurityUser user) throws IOException, HttpException {
        httpClient.putJson(API_SECURITY_USERS, user);
    }

    /**
     * Get this app all security resource entities
     *
     * @return this app all security resource entities
     * @throws IOException
     * @throws HttpException
     */
    public List<SecurityResourceEntity> getAllResourceEntities() throws IOException, HttpException {
        String content = httpClient.get(API_SECURITY_RESOURCE_ENTITIES);
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
        String content = httpClient.get(API_SECURITY_NAVS);
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
        String content = httpClient.get(API_SECURITY_AUTHORITIES);
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
        String content = httpClient.get(API_SECURITY_ROLES);
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
        String content = httpClient.get(API_SECURITY_GROUPS);
        return JSONArray.parseArray(content, SecurityGroup.class);
    }
}
