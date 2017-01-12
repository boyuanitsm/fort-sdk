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
import java.util.List;

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
     * @param user   security user, login, passwordHash required
     * @param roles  the user roles
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
            String content = httpClient.get(String.format("%s/%s", API.SECURITY_USER_BY_LOGIN, login));
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
     * @throws FortCrudException
     * @throws IOException
     */
    public SecurityGroup updateSecurityGroup(SecurityGroup securityGroup) throws FortCrudException, IOException {
        return mapper.readValue(httpClient.putJson(API.SECURITY_GROUPS, securityGroup), SecurityGroup.class);
    }

    /**
     * GET  /security-groups : get all the securityGroups.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of securityGroups in body
     * @throws FortCrudException
     * @throws IOException
     */
    public List<SecurityGroup> getAllSecurityGroup(Pageable pageable) throws FortCrudException, IOException {
        return mapper.readValue(httpClient.get(API.SECURITY_GROUPS,
                new BasicNameValuePair("page", String.valueOf(pageable.getPage())),
                new BasicNameValuePair("size", String.valueOf(pageable.getSize()))),
                TypeFactory.defaultInstance().constructCollectionType(List.class, SecurityGroup.class));
    }

    /**
     * GET  /security-groups/:id :  the "id" securityGroup
     *
     * @param id the id of the securityGroup to retrieve
     * @return the  securityGroup, or null (Not Found)
     */
    public SecurityGroup getSecurityGroup(Long id) throws FortCrudException, IOException {
        return mapper.readValue(httpClient.get(String.format("%s/%s", API.SECURITY_GROUPS, id)), SecurityGroup.class);
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

    /**
     * POST  /security-resource-entities : Create a new securityResourceEntity.
     *
     * @param securityResourceEntity the securityResourceEntity to create
     * @return the ResponseEntity with status 201 (Created) and with body the new securityResourceEntity, or with throw {@link FortNoValidException}
     * 400 (Bad Request) if the securityResourceEntity has already an ID
     * @throws FortCrudException
     * @throws IOException
     */
    public SecurityResourceEntity createSecurityResourceEntity(SecurityResourceEntity securityResourceEntity) throws FortCrudException, IOException {
        return mapper.readValue(httpClient.postJson(API.SECURITY_RESOURCE_ENTITIES, securityResourceEntity), SecurityResourceEntity.class);
    }

    /**
     * PUT  /security-resource-entities : Updates an existing securityGroup.
     *
     * @param securityResourceEntity the securityResourceEntity to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated securityResourceEntity,
     * or throw {@link FortNoValidException}  if the securityResourceEntity is not valid,
     * or throw {@link FortCrudException} (Internal Server Error) if the securityResourceEntity couldnt be updated
     * @throws FortCrudException
     * @throws IOException
     */
    public SecurityResourceEntity updateSecurityResourceEntity(SecurityResourceEntity securityResourceEntity) throws FortCrudException, IOException {
        return mapper.readValue(httpClient.putJson(API.SECURITY_RESOURCE_ENTITIES, securityResourceEntity), SecurityResourceEntity.class);
    }

    /**
     * GET  /security-resource-entities : get all the securityResourceEntities.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of securityResourceEntities in body
     * @throws FortCrudException
     * @throws IOException
     */
    public List<SecurityResourceEntity> getAllSecurityResourceEntity(Pageable pageable) throws FortCrudException, IOException {
        return mapper.readValue(httpClient.get(API.SECURITY_RESOURCE_ENTITIES,
                new BasicNameValuePair("page", String.valueOf(pageable.getPage())),
                new BasicNameValuePair("size", String.valueOf(pageable.getSize()))),
                TypeFactory.defaultInstance().constructCollectionType(List.class, SecurityResourceEntity.class));
    }

    /**
     * GET  /security-resource-entities/:id : get the "id" securityResourceEntity.
     *
     * @param id the id of the securityResourceEntity to retrieve
     * @return the  securityGroup, or null (Not Found)
     * @throws FortCrudException
     * @throws IOException
     */
    public SecurityResourceEntity getSecurityResourceEntity(Long id) throws FortCrudException, IOException {
        // return resourceManager.getResourceEntity(id);
        return mapper.readValue(httpClient.get(String.format("%s/%s", API.SECURITY_RESOURCE_ENTITIES, id)), SecurityResourceEntity.class);
    }

    /**
     * DELETE  /security-resource-entities/:id : delete the "id" securityResourceEntity.
     *
     * @param id the id of the securityResourceEntity to delete
     */
    public void deleteSecurityResourceEntity(Long id) throws FortCrudException {
        httpClient.delete(String.format("%s/%s", API.SECURITY_RESOURCE_ENTITIES, id));
    }

    // ============= End: Security Resource Entity Crud ====================

    // ============= Start: Security Nav Crud ====================

    /**
     * POST  /security-navs : Create a new securityNav.
     *
     * @param securityNav the securityNav to create
     * @return the ResponseEntity with status 201 (Created) and with body the new securityNav, or with throw {@link FortNoValidException}
     * 400 (Bad Request) if the securityNav has already an ID
     * @throws FortCrudException
     * @throws IOException
     */
    public SecurityNav createSecurityNav(SecurityNav securityNav) throws FortCrudException, IOException {
        return mapper.readValue(httpClient.postJson(API.SECURITY_NAVS, securityNav), SecurityNav.class);
    }

    /**
     * PUT  /security-navs : Updates an existing securityNav.
     *
     * @param securityNav the securityNav to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated securityNav,
     * or throw {@link FortNoValidException}  if the securityNav is not valid,
     * or throw {@link FortCrudException} (Internal Server Error) if the securityNav couldnt be updated
     * @throws FortCrudException
     * @throws IOException
     */
    public SecurityNav updateSecurityNav(SecurityNav securityNav) throws FortCrudException, IOException {
        return mapper.readValue(httpClient.putJson(API.SECURITY_NAVS, securityNav), SecurityNav.class);
    }

    /**
     * GET  /security-navs : get all the securityNavs.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of securityNavs in body
     * @throws FortCrudException
     * @throws IOException
     */
    public List<SecurityNav> getAllSecurityNav(Pageable pageable) throws FortCrudException, IOException {
        return mapper.readValue(httpClient.get(API.SECURITY_NAVS,
                new BasicNameValuePair("page", String.valueOf(pageable.getPage())),
                new BasicNameValuePair("size", String.valueOf(pageable.getSize()))),
                TypeFactory.defaultInstance().constructCollectionType(List.class, SecurityNav.class));
    }

    /**
     * GET  /security-navs/:id : get the "id" securityNav.
     *
     * @param id the id of the securityNav to retrieve
     * @return the  securityNav, or null (Not Found)
     */
    public SecurityNav getSecurityNav(Long id) throws FortCrudException, IOException {
        return mapper.readValue(httpClient.get(String.format("%s/%s", API.SECURITY_NAVS, id)), SecurityNav.class);
    }

    /**
     * DELETE  /security-navs/:id : delete the "id" securityNav.
     *
     * @param id the id of the securityNav to delete
     */
    public void deleteSecurityNav(Long id) throws FortCrudException {
        httpClient.delete(String.format("%s/%s", API.SECURITY_NAVS, id));
    }

    // ============= End: Security Nav Crud ====================

    // ============= Start: Security Authority Crud ====================

    /**
     * POST  /security-authorities : Create a new securityNav.
     *
     * @param securityAuthority the securityAuthority to create
     * @return the ResponseEntity with status 201 (Created) and with body the new securityAuthority, or with throw {@link FortNoValidException}
     * 400 (Bad Request) if the securityAuthority has already an ID
     * @throws FortCrudException
     * @throws IOException
     */
    public SecurityAuthority createSecurityAuthority(SecurityAuthority securityAuthority) throws FortCrudException, IOException {
        return mapper.readValue(httpClient.postJson(API.SECURITY_AUTHORITIES, securityAuthority), SecurityAuthority.class);
    }

    /**
     * PUT  /security-authorities : Updates an existing securityAuthority.
     *
     * @param securityAuthority the securityAuthority to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated securityAuthority,
     * or throw {@link FortNoValidException}  if the securityAuthority is not valid,
     * or throw {@link FortCrudException} (Internal Server Error) if the securityAuthority couldnt be updated
     * @throws FortCrudException
     * @throws IOException
     */
    public SecurityAuthority updateSecurityAuthority(SecurityAuthority securityAuthority) throws FortCrudException, IOException {
        return mapper.readValue(httpClient.putJson(API.SECURITY_AUTHORITIES, securityAuthority), SecurityAuthority.class);
    }

    /**
     * GET  /security-authorities : get all the securityAuthority.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of securityAuthority in body
     * @throws FortCrudException
     * @throws IOException
     */
    public List<SecurityAuthority> getAllSecurityAuthority(Pageable pageable) throws FortCrudException, IOException {
        return mapper.readValue(httpClient.get(API.SECURITY_AUTHORITIES,
                new BasicNameValuePair("page", String.valueOf(pageable.getPage())),
                new BasicNameValuePair("size", String.valueOf(pageable.getSize()))),
                TypeFactory.defaultInstance().constructCollectionType(List.class, SecurityAuthority.class));
    }

    /**
     * GET  /security-authorities/:id : get the "id" securityAuthority.
     *
     * @param id the id of the securityAuthority to retrieve
     * @return the  securityAuthority, or null (Not Found)
     */
    public SecurityAuthority getSecurityAuthority(Long id) throws FortCrudException, IOException {
        return mapper.readValue(httpClient.get(String.format("%s/%s", API.SECURITY_AUTHORITIES, id)), SecurityAuthority.class);
    }

    /**
     * DELETE  /security-authorities/:id : delete the "id" securityAuthority.
     *
     * @param id the id of the securityAuthority to delete
     */
    public void deleteSecurityAuthority(Long id) throws FortCrudException {
        httpClient.delete(String.format("%s/%s", API.SECURITY_AUTHORITIES, id));
    }

    // ============= End: Security Authority Crud ====================

    // ============= Start: Security Role Crud ====================

    /**
     * POST  /security-roles : Create a new securityRole.
     *
     * @param securityRole the securityRole to create
     * @return the ResponseEntity with status 201 (Created) and with body the new securityRole, or with throw {@link FortNoValidException}
     * 400 (Bad Request) if the securityRole has already an ID
     * @throws FortCrudException
     * @throws IOException
     */
    public SecurityRole createSecurityRole(SecurityRole securityRole) throws FortCrudException, IOException {
        return mapper.readValue(httpClient.postJson(API.SECURITY_ROLES, securityRole), SecurityRole.class);
    }

    /**
     * PUT  /security-authorities : Updates an existing securityRole.
     *
     * @param securityRole the securityRole to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated securityRole,
     * or throw {@link FortNoValidException}  if the securityRole is not valid,
     * or throw {@link FortCrudException} (Internal Server Error) if the securityRole couldnt be updated
     * @throws FortCrudException
     * @throws IOException
     */
    public SecurityRole updateSecurityRole(SecurityRole securityRole) throws FortCrudException, IOException {
        return mapper.readValue(httpClient.putJson(API.SECURITY_ROLES, securityRole), SecurityRole.class);
    }

    /**
     * GET  /security-roles : get all the securityRole.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of securityRole in body
     * @throws FortCrudException
     * @throws IOException
     */
    public List<SecurityRole> getAllSecurityRole(Pageable pageable) throws FortCrudException, IOException {
        return mapper.readValue(httpClient.get(API.SECURITY_ROLES,
                new BasicNameValuePair("page", String.valueOf(pageable.getPage())),
                new BasicNameValuePair("size", String.valueOf(pageable.getSize()))),
                TypeFactory.defaultInstance().constructCollectionType(List.class, SecurityRole.class));
    }

    /**
     * GET  /security-roles/:id : get the "id" securityRole.
     *
     * @param id the id of the securityRole to retrieve
     * @return the  securityRole, or null (Not Found)
     */
    public SecurityRole getSecurityRole(Long id) throws FortCrudException, IOException {
        return mapper.readValue(httpClient.get(String.format("%s/%s", API.SECURITY_ROLES, id)), SecurityRole.class);
    }

    /**
     * DELETE  /security-roles/:id : delete the "id" securityRole.
     *
     * @param id the id of the securityRole to delete
     */
    public void deleteSecurityRole(Long id) throws FortCrudException {
        httpClient.delete(String.format("%s/%s", API.SECURITY_ROLES, id));
    }

    // ============= End: Security Role Crud ====================
}
