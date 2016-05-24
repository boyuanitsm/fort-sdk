package com.boyuanitsm.fort.sdk.client;

import com.boyuanitsm.fort.sdk.cache.FortResourceCache;
import com.boyuanitsm.fort.sdk.config.FortConfiguration;
import com.boyuanitsm.fort.sdk.domain.SecurityUser;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * A fort crud client. support all crud of the fort server.
 *
 * @author zhanghua on 5/24/16.
 */
@Component
public class FortCrudClient {

    @Autowired
    private FortResourceCache cache;
    @Autowired
    private FortConfiguration configuration;
    @Autowired
    private FortHttpClient fortHttpClient;

    private static final String API_SECURITY_USERS = "/api/security-users";

    /**
     * Register a new user, before register, set default role,group.
     * role is fort.yml user: defaultRole. multi value comma split.
     * group is fort.yml user: defaultGroup. multi value comma split.
     *
     * @param user security user, login、passwordHash required
     * @throws IOException
     * @throws HttpException
     */
    public void signUp(SecurityUser user) throws IOException, HttpException {
        // set default role, group
        user.setRoles(cache.getRolesByArrayNames(configuration.getUser().getDefaultRoles()));
        user.setGroups(cache.getGroupsByArrayNames(configuration.getUser().getDefaultGroups()));
        user.setActivated(true);

        fortHttpClient.postJson(API_SECURITY_USERS, user);
    }
}
