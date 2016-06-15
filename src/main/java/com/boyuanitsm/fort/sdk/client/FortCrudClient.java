package com.boyuanitsm.fort.sdk.client;

import com.boyuanitsm.fort.sdk.cache.FortResourceCache;
import com.boyuanitsm.fort.sdk.config.API;
import com.boyuanitsm.fort.sdk.config.FortConfiguration;
import com.boyuanitsm.fort.sdk.context.FortContextHolder;
import com.boyuanitsm.fort.sdk.domain.SecurityGroup;
import com.boyuanitsm.fort.sdk.domain.SecurityUser;
import com.boyuanitsm.fort.sdk.exception.FortCrudException;
import com.boyuanitsm.fort.sdk.exception.FortNoValidException;
import com.boyuanitsm.fort.sdk.util.ObjectMapperBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private ObjectMapper mapper = ObjectMapperBuilder.build();

    // ============= Start: Security User Crud ====================

    /**
     * Register a new user, before register, set default role,group.
     * role is fort.yml user: defaultRole. multi value comma split.
     * group is fort.yml user: defaultGroup. multi value comma split.
     *
     * @param user security user, login, passwordHash required
     * @return new user
     * @throws FortCrudException
     */
    public SecurityUser signUp(SecurityUser user) throws FortCrudException, IOException {
        if (user == null) {
            throw new FortCrudException("user is can't be null!");
        }
        if (user.getLogin() == null || user.getLogin().isEmpty()) {
            throw new FortCrudException("user.login is can't be null!");
        }
        if (user.getPasswordHash() == null || user.getPasswordHash().isEmpty()) {
            throw new FortCrudException("user.passwordHash is can't be null!");
        }
        return signUp(user, configuration.getUser().getDefaultRoles(), configuration.getUser().getDefaultGroups());
    }

    /**
     * Register a new user, using given roles and groups
     *
     * @param user security user, login, passwordHash required
     * @param roles the user roles
     * @param groups the user groups
     * @return new user
     * @throws FortCrudException
     * @throws IOException
     */
    public SecurityUser signUp(SecurityUser user, String[] roles, String[] groups) throws FortCrudException, IOException {
        // set default role, group
        if (roles != null) {
            user.setRoles(cache.getRolesByArrayNames(roles));
        }
        if (groups != null) {
            user.setGroups(cache.getGroupsByArrayNames(groups));
        }
        // activated
        user.setActivated(true);

        String content = fortHttpClient.postJson(API.SECURITY_USERS, user);
        return mapper.readValue(content, SecurityUser.class);
    }

    /**
     * Change current logged user password. if no logged, do nothing.
     *
     * @param newPassword the new password of the SecurityUser
     * @throws FortCrudException
     * @throws IOException
     */
    public void changeCurrentUserPassword(String newPassword) throws FortCrudException, IOException {
        SecurityUser user = FortContextHolder.getContext().getSecurityUser();
        if (user == null) {
            return;
        }
        user.setPasswordHash(newPassword);
        fortHttpClient.putJson(API.SECURITY_USERS, user);
    }

    // ============= End: Security User Crud ====================


    // ============= Start: Security Group Crud ====================

    /**
     * POST  /security-groups : Create a new securityGroup.
     *
     * @param securityGroup the securityGroup to create
     * @return the ResponseEntity with status 201 (Created) and with body the new securityGroup, or with throw {@link FortNoValidException}
     * 400 (Bad Request) if the securityGroup has already an ID
     * @throws FortCrudException
     * @throws IOException
     */
    public SecurityGroup createSecurityGroup(SecurityGroup securityGroup) throws FortCrudException, IOException {
        return mapper.readValue(fortHttpClient.postJson(API.SECURITY_GROUPS, securityGroup), SecurityGroup.class);
    }

    /**
     * PUT  /security-groups : Updates an existing securityGroup.
     *
     * @param securityGroup the securityGroup to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated securityGroup,
     * or throw {@link FortNoValidException}  if the securityGroup is not valid,
     * or throw {@link FortCrudException} (Internal Server Error) if the securityGroup couldnt be updated
     * @throws FortCrudException if the Location URI syntax is incorrect
     * @throws IOException
     */
    public SecurityGroup updateSecurityGroup(SecurityGroup securityGroup) throws FortCrudException, IOException {
        return mapper.readValue(fortHttpClient.putJson(API.SECURITY_GROUPS, securityGroup), SecurityGroup.class);
    }

    /**
     * Get all the securityGroups from cache.
     */
    public List<SecurityGroup> getAllSecurityGroup() {
        Map<Long, SecurityGroup> groupMap = cache.getGroupCache();
        List<SecurityGroup> groups = new ArrayList<SecurityGroup>();
        for (Long key : groupMap.keySet()) {
            groups.add(groupMap.get(key));
        }
        return groups;
    }

    /**
     * Get the "id" securityGroup
     *
     * @param id the id of the securityGroup to retrieve
     * @return the  securityGroup, or null (Not Found)
     */
    public SecurityGroup getSecurityGroup(Long id) {
        return cache.getGroupCache().get(id);
    }

    /**
     * DELETE  /security-groups/:id : delete the "id" securityGroup.
     *
     * @param id the id of the securityGroup to delete
     */
    public void deleteSecurityGroup(Long id) throws FortCrudException {
        fortHttpClient.delete(String.format("%s/%s", API.SECURITY_GROUPS, id));
    }

    // ============= End: Security Group Crud ====================
}
