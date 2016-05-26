package com.boyuanitsm.fort.sdk.client;

import com.boyuanitsm.fort.sdk.cache.FortResourceCache;
import com.boyuanitsm.fort.sdk.config.FortConfiguration;
import com.boyuanitsm.fort.sdk.domain.SecurityGroup;
import com.boyuanitsm.fort.sdk.domain.SecurityUser;
import com.boyuanitsm.fort.sdk.exception.FortCrudException;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

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

    // ============= Start: Security User Crud ====================

    /**
     * Register a new user, before register, set default role,group.
     * role is fort.yml user: defaultRole. multi value comma split.
     * group is fort.yml user: defaultGroup. multi value comma split.
     *
     * @param user security user, login、passwordHash required
     * @throws FortCrudException
     */
    public void signUp(SecurityUser user) throws FortCrudException {
        // set default role, group
        user.setRoles(cache.getRolesByArrayNames(configuration.getUser().getDefaultRoles()));
        user.setGroups(cache.getGroupsByArrayNames(configuration.getUser().getDefaultGroups()));
        user.setActivated(true);

        try {
            fortHttpClient.postJson(API_SECURITY_USERS, user);
        } catch (IOException | HttpException e) {
            throw new FortCrudException(e);
        }
    }

    // ============= End: Security User Crud ====================


    // ============= Start: Security Group Crud ====================

    public SecurityGroup createSecurityGroup(SecurityGroup securityGroup) throws FortCrudException {
        return null;
    }

    public SecurityGroup updateSecurityGroup(SecurityGroup securityGroup) {
        return null;
    }

    public List<SecurityGroup> getAllSecurityGroup() {
        return null;
    }

    public SecurityGroup getSecurityGroup(Long id) {
        return null;
    }

    public void deleteSecurityGroup(Long id) {

    }

    // ============= End: Security Group Crud ====================
}
