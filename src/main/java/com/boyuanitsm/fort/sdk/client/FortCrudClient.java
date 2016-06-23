package com.boyuanitsm.fort.sdk.client;

import com.boyuanitsm.fort.sdk.bean.Pageable;
import com.boyuanitsm.fort.sdk.ResourceManager;
import com.boyuanitsm.fort.sdk.config.API;
import com.boyuanitsm.fort.sdk.config.FortProperties;
import com.boyuanitsm.fort.sdk.context.FortContextHolder;
import com.boyuanitsm.fort.sdk.domain.*;
import com.boyuanitsm.fort.sdk.exception.FortCrudException;
import com.boyuanitsm.fort.sdk.exception.FortNoValidException;
import com.boyuanitsm.fort.sdk.util.ObjectMapperBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger log = LoggerFactory.getLogger(FortCrudClient.class);

    @Autowired
    private ResourceManager resourceManager;
    @Autowired
    private FortProperties fortProperties;
    @Autowired
    private HttpClient httpClient;

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
        return signUp(user, fortProperties.getUser().getDefaultRoles(), fortProperties.getUser().getDefaultGroups());
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
            user.setRoles(resourceManager.getRolesByArrayNames(roles));
        }
        if (groups != null) {
            user.setGroups(resourceManager.getGroupsByArrayNames(groups));
        }
        // activated
        user.setActivated(true);

        String content = httpClient.postJson(API.SECURITY_USERS, user);
        return mapper.readValue(content, SecurityUser.class);
    }

    /**
     * GET  /security-users : get all the securityUsers.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of securityUsers in body
     * @throws FortCrudException
     * @throws IOException
     */
    public List<SecurityUser> getAllSecurityUser(Pageable pageable) throws FortCrudException, IOException {
        if (pageable == null) {
            pageable = new Pageable();
        }
        return mapper.readValue(httpClient.get(API.SECURITY_USERS,
                new BasicNameValuePair("page", String.valueOf(pageable.getPage())),
                new BasicNameValuePair("size", String.valueOf(pageable.getSize()))),
                    TypeFactory.defaultInstance().constructCollectionType(List.class, SecurityUser.class));
    }

    /**
     * Get security user by login
     *
     * @param login the login of the security user
     * @return if not found return null
     * @throws FortCrudException
     * @throws IOException
     */
    public SecurityUser getSecurityUser(String login) throws FortCrudException, IOException {
        try {
            String content = httpClient.get(API.SECURITY_USER_BY_LOGIN + "/" + login);
            return mapper.readValue(content, SecurityUser.class);
        } catch (FortCrudException e) {
            log.warn("Not found {} user.", login, e.getMessage());
            return null;
        }
    }

    /**
     * PUT /security-users : Updates an existing securityUser.
     *
     * @param securityUser the securityUser to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated securityGroup,
     * or throw {@link FortNoValidException}  if the securityUser is not valid,
     * or throw {@link FortCrudException} (Internal Server Error) if the securityUser couldnt be updated
     * @throws FortCrudException
     * @throws IOException
     */
    public SecurityUser updateSecurityUser(SecurityUser securityUser) throws FortCrudException, IOException {
        return mapper.readValue(httpClient.putJson(API.SECURITY_USERS, securityUser), SecurityUser.class);
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
        httpClient.putJson(API.SECURITY_USERS, user);
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
        return mapper.readValue(httpClient.postJson(API.SECURITY_GROUPS, securityGroup), SecurityGroup.class);
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
        return mapper.readValue(httpClient.putJson(API.SECURITY_GROUPS, securityGroup), SecurityGroup.class);
    }

    /**
     * Get all the securityGroups from resourceManager.
     */
    public List<SecurityGroup> getAllSecurityGroup() {
        Map<Long, SecurityGroup> groupMap = resourceManager.getGroupCache();
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
        return resourceManager.getGroupCache().get(id);
    }

    /**
     * DELETE  /security-groups/:id : delete the "id" securityGroup.
     *
     * @param id the id of the securityGroup to delete
     */
    public void deleteSecurityGroup(Long id) throws FortCrudException {
        httpClient.delete(String.format("%s/%s", API.SECURITY_GROUPS, id));
    }

    // ============= End: Security Group Crud ====================

    // ============= Start: Security Resource Entity Crud ====================

    public SecurityResourceEntity createSecurityResourceEntity(SecurityResourceEntity securityResourceEntity) throws FortCrudException, IOException {
        return null;
    }

    public SecurityResourceEntity updateSecurityResourceEntity(SecurityResourceEntity securityResourceEntity) throws FortCrudException, IOException {
        return null;
    }

    public List<SecurityResourceEntity> getAllSecurityResourceEntity(SecurityResourceEntity securityResourceEntity) {
        return null;
    }

    public SecurityResourceEntity getSecurityResourceEntity(Long id) {
        return null;
    }

    public void deleteSecurityResourceEntity(Long id) throws FortCrudException {

    }

    // ============= End: Security Resource Entity Crud ====================

    // ============= Start: Security Nav Crud ====================

    public SecurityNav createSecurityNav(SecurityNav securityNav) throws FortCrudException, IOException {
        return null;
    }

    public SecurityNav updateSecurityNav(SecurityNav securityNav) throws FortCrudException, IOException {
        return null;
    }

    public List<SecurityNav> getAllSecurityNav(SecurityNav securityNav) {
        return null;
    }

    public SecurityNav getSecurityNav(Long id) {
        return null;
    }

    public void deleteSecurityNav(Long id) throws FortCrudException {

    }

    // ============= End: Security Nav Crud ====================

    // ============= Start: Security Authority Crud ====================

    public SecurityAuthority createSecurityAuthority(SecurityAuthority securityAuthority) throws FortCrudException, IOException {
        return null;
    }

    public SecurityAuthority updateSecurityAuthority(SecurityAuthority securityAuthority) throws FortCrudException, IOException {
        return null;
    }

    public List<SecurityAuthority> getAllSecurityAuthority(SecurityAuthority securityAuthority) {
        return null;
    }

    public SecurityAuthority getSecurityAuthority(Long id) {
        return null;
    }

    public void deleteSecurityAuthority(Long id) throws FortCrudException {

    }

    // ============= End: Security Authority Crud ====================

    // ============= Start: Security Role Crud ====================

    public SecurityRole createSecurityRole(SecurityRole securityRole) throws FortCrudException, IOException {
        return null;
    }

    public SecurityRole updateSecurityRole(SecurityRole securityRole) throws FortCrudException, IOException {
        return null;
    }

    public List<SecurityRole> getAllSecurityRole(SecurityRole securityRole) {
        return null;
    }

    public SecurityRole getSecurityRole(Long id) {
        return null;
    }

    public void deleteSecurityRole(Long id) throws FortCrudException {

    }

    // ============= End: Security Role Crud ====================
}
