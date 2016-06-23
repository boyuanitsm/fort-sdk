package com.boyuanitsm.fort.sdk.client;

import com.boyuanitsm.fort.sdk.config.API;
import com.boyuanitsm.fort.sdk.config.FortProperties;
import com.boyuanitsm.fort.sdk.domain.*;
import com.boyuanitsm.fort.sdk.exception.FortAuthenticationException;
import com.boyuanitsm.fort.sdk.exception.FortCrudException;
import com.boyuanitsm.fort.sdk.util.ObjectMapperBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An fort manager client. packaging fort manager http interface.
 *
 * @author zhanghua on 5/16/16.
 */
@Component
public class ManagerClient {

    private CookieStore cookieStore;
    private HttpClient httpClient;
    private ObjectMapper mapper;

    @Autowired
    public ManagerClient(FortProperties fortProperties, HttpClient httpClient) throws FortCrudException {
        mapper = ObjectMapperBuilder.build();
        this.httpClient = httpClient;
        this.cookieStore = loginFortSecurityServer(fortProperties.getApp().getAppKey(), fortProperties.getApp().getAppSecret());
    }

    /**
     * login fort context server. if error throw new RuntimeException
     *
     * @param appKey the spring security j_username
     * @param secret the spring security j_password
     * @throws FortCrudException
     */
    private CookieStore loginFortSecurityServer(String appKey, String secret) throws FortCrudException {
        return httpClient.loginFortSecurityServer(API.AUTHENTICATION,
                new BasicNameValuePair("j_username", appKey),
                new BasicNameValuePair("j_password", secret),
                new BasicNameValuePair("remember-me", "true"),
                new BasicNameValuePair("submit", "Login"));
    }

    /**
     * signIn access this app server
     *
     * @param login    not null
     * @param password not null, raw password
     * @throws FortAuthenticationException if validate failure, throw exception
     */
    public SecurityUser signIn(String login, String password, String ipAddress, String userAgent) throws FortAuthenticationException {
        Map<String, Object> obj = new HashMap<>();
        obj.put("login", login);
        obj.put("passwordHash", password);
        obj.put("ipAddress", ipAddress);
        obj.put("userAgent", userAgent);
        try {
            String content = httpClient.postJson(API.SECURITY_USER_AUTHORIZATION, obj);
            return mapper.readValue(content, SecurityUser.class);
        } catch (FortCrudException | IOException e) {
            throw new FortAuthenticationException("security user sign in failure!", e);
        }
    }

    /**
     * Get this app all security resource entities
     *
     * @return this app all security resource entities
     * @throws FortCrudException
     * @throws IOException
     */
    public List<SecurityResourceEntity> getAllResourceEntities() throws FortCrudException, IOException {
        String content = httpClient.get(API.SA_SECURITY_RESOURCE_ENTITIES);
        return mapper.readValue(content, TypeFactory.defaultInstance().constructCollectionType(List.class, SecurityResourceEntity.class));
    }

    /**
     * Get this app all security navs.
     *
     * @return this app all security navs.
     * @throws FortCrudException
     * @throws IOException
     */
    public List<SecurityNav> getAllSecurityNavs() throws FortCrudException, IOException {
        String content = httpClient.get(API.SECURITY_NAVS);
        return mapper.readValue(content, TypeFactory.defaultInstance().constructCollectionType(List.class, SecurityNav.class));
    }

    /**
     * Get this app all security authorities.
     *
     * @return this app all security authorities.
     * @throws FortCrudException
     * @throws IOException
     */
    public List<SecurityAuthority> getAllAuthorities() throws FortCrudException, IOException {
        String content = httpClient.get(API.SA_SECURITY_AUTHORITIES);
        return mapper.readValue(content, TypeFactory.defaultInstance().constructCollectionType(List.class, SecurityAuthority.class));
    }

    /**
     * Get this app all security roles.
     *
     * @return this app all security roles.
     * @throws FortCrudException
     * @throws IOException
     */
    public List<SecurityRole> getAllRoles() throws FortCrudException, IOException {
        String content = httpClient.get(API.SA_SECURITY_ROLES);
        return mapper.readValue(content, TypeFactory.defaultInstance().constructCollectionType(List.class, SecurityRole.class));
    }

    /**
     * Get this app all security groups.
     *
     * @return this app all security groups.
     * @throws FortCrudException
     * @throws IOException
     */
    public List<SecurityGroup> getAllGroups() throws FortCrudException, IOException {
        String content = httpClient.get(API.SECURITY_GROUPS);
        return mapper.readValue(content, TypeFactory.defaultInstance().constructCollectionType(List.class, SecurityGroup.class));
    }

    /**
     * Get user by user token.
     *
     * @param token the token of the user.
     * @return user
     * @throws FortCrudException
     */
    public SecurityUser getByUserToken(String token) throws FortCrudException, IOException {
        String content = httpClient.get(API.SECURITY_USERS_DTO + "/" + token);
        if (content == null) {
            return null;
        }
        return mapper.readValue(content, SecurityUser.class);
    }

    public String getCookieString() {
        String cookieString = "";

        for (Cookie cookie : cookieStore.getCookies()) {
            cookieString += cookie.getName() + "=" + cookie.getValue() + ";";
        }

        return cookieString;
    }

    public void logout(String token) throws FortCrudException, JsonProcessingException {
        Map<String, Object> json = new HashMap<>();
        json.put("tokenValue", token);
        httpClient.putJson(API.SECURITY_USER_LOGOUT, json);
    }
}
