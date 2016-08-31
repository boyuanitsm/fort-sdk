package com.boyuanitsm.fort.sdk;

import com.boyuanitsm.fort.sdk.bean.OnUpdateSecurityResource;
import com.boyuanitsm.fort.sdk.bean.enumeration.OnUpdateSecurityResourceClass;
import com.boyuanitsm.fort.sdk.bean.enumeration.OnUpdateSecurityResourceOption;
import com.boyuanitsm.fort.sdk.client.ManagerClient;
import com.boyuanitsm.fort.sdk.context.FortContext;
import com.boyuanitsm.fort.sdk.domain.*;
import com.boyuanitsm.fort.sdk.exception.FortCrudException;
import com.boyuanitsm.fort.sdk.util.ObjectMapperBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

import static com.boyuanitsm.fort.sdk.bean.enumeration.OnUpdateSecurityResourceClass.*;
import static com.boyuanitsm.fort.sdk.bean.enumeration.OnUpdateSecurityResourceOption.*;

/**
 * fort resource cache. cache resource entity, nav, authority, role, group.
 *
 * @author zhanghua on 5/17/16.
 */
@Component
public class ResourceManager {

    private final Logger log = LoggerFactory.getLogger(ResourceManager.class);

    private ManagerClient managerClient;

    /**
     * resource entity cache. SecurityResourceEntity.id - SecurityResourceEntity
     */
    private static Map<Long, SecurityResourceEntity> resourceEntityCache;
    /**
     * resource url id map.  SecurityResourceEntity.url - SecurityResourceEntity.id.
     */
    private static Map<String, Long> resourceUrlIdMap;
    /**
     * resource id - authority ids
     */
    private static Map<Long, Set<Long>> resourceAuthoritiesMap;
    /**
     * nav cache. SecurityResourceEntity.id - SecurityNav.
     */
    private static Map<Long, SecurityNav> navCache;
    /**
     * authority cache. SecurityAuthority.id - SecurityAuthority.
     */
    private static Map<Long, SecurityAuthority> authorityCache;
    /**
     * role cache. SecurityRole.id - SecurityRole.
     */
    private static Map<Long, SecurityRole> roleCache;
    /**
     * group cache. SecurityGroup.id - SecurityGroup.
     */
    private static Map<Long, SecurityGroup> groupCache;

    /**
     * logged user cache. SecurityUser.token - SecurityUser.
     */
    private static Map<String, SecurityUser> loggedUserCache;

    private ObjectMapper mapper;

    @Autowired
    public ResourceManager(ManagerClient managerClient) throws FortCrudException, IOException {
        mapper = ObjectMapperBuilder.build();
        this.managerClient = managerClient;// autowired fort client
        load();// load resource
    }

    /**
     * init cache. new HashMap.
     */
    private void initCache() {
        resourceEntityCache = new HashMap<>();
        resourceUrlIdMap = new HashMap<>();
        navCache = new HashMap<>();
        authorityCache = new HashMap<>();
        roleCache = new HashMap<>();
        groupCache = new HashMap<>();
        loggedUserCache = new HashMap<>();
        resourceAuthoritiesMap = new HashMap<>();
    }

    /**
     * load resource from fort server.
     *
     * @throws FortCrudException
     */
    private void load() throws FortCrudException, IOException {
        log.info("Starting fort");
        long t1 = System.currentTimeMillis();

        initCache();

        // load resource entities
        List<SecurityResourceEntity> resourceEntities = managerClient.getAllResourceEntities();
        for (SecurityResourceEntity resourceEntity : resourceEntities) {
            resourceEntityCache.put(resourceEntity.getId(), resourceEntity);
            resourceUrlIdMap.put(resourceEntity.getUrl(), resourceEntity.getId());
        }
        // load navs
        List<SecurityNav> navs = managerClient.getAllSecurityNavs();
        for (SecurityNav nav : navs) {
            if (nav.getResource() != null) {
                navCache.put(nav.getResource().getId(), nav);
            }
        }
        // load authorities
        List<SecurityAuthority> authorities = managerClient.getAllAuthorities();
        for (SecurityAuthority authority : authorities) {
            authorityCache.put(authority.getId(), authority);
            loadResourceAuthoritiesIdsMap(authority);
        }
        // load roles
        List<SecurityRole> roles = managerClient.getAllRoles();
        for (SecurityRole role : roles) {
            roleCache.put(role.getId(), role);
        }
        // load group
        List<SecurityGroup> groups = managerClient.getAllGroups();
        for (SecurityGroup group : groups) {
            groupCache.put(group.getId(), group);
        }

        log.info("Started fort in {} ms", System.currentTimeMillis() - t1);
    }

    /**
     * Load resource id - authority ids map
     *
     * @param authority the SecurityAuthority
     */
    private void loadResourceAuthoritiesIdsMap(SecurityAuthority authority) {
        for (SecurityResourceEntity resourceEntity: authority.getResources()) {
            Set<Long> authorities = resourceAuthoritiesMap.get(resourceEntity.getId());
            if (authorities == null) {
                authorities = new HashSet<>();
                resourceAuthoritiesMap.put(resourceEntity.getId(), authorities);
            }
            authorities.add(authority.getId());
        }
    }

    public Set<Long> getAuthorityIdSet(Long resourceId) {
        return resourceAuthoritiesMap.get(resourceId);
    }

    /**
     * get security role by name.
     *
     * @param name role name
     * @return if not found return null
     */
    public SecurityRole getSecurityRoleByName(String name) {
        for (Long key: roleCache.keySet()) {
            SecurityRole role = roleCache.get(key);
            if (role.getName().equals(name)) {
                return role;
            }
        }
        return null;
    }

    /**
     * get security group by name.
     *
     * @param name group name
     * @return if not found return null
     */
    public SecurityGroup getSecurityGroupByName(String name) {
        for (Long key: groupCache.keySet()) {
            SecurityGroup group = groupCache.get(key);
            if (group.getName().equals(name)) {
                return group;
            }
        }
        return null;
    }

    public Map<Long, SecurityResourceEntity> getResourceEntityCache() {
        return resourceEntityCache;
    }

    public Map<Long, SecurityAuthority> getAuthorityCache() {
        return authorityCache;
    }

    public Map<Long, SecurityNav> getNavCache() {
        return navCache;
    }

    public Map<Long, SecurityRole> getRoleCache() {
        return roleCache;
    }

    public Map<Long, SecurityGroup> getGroupCache() {
        return groupCache;
    }

    public Long getResourceId(String url) {
        return resourceUrlIdMap.get(url);
    }

    public SecurityResourceEntity getResourceEntity(Long id) {
        return resourceEntityCache.get(id);
    }

    public SecurityRole getRole(Long id) {
        return roleCache.get(id);
    }

    public Set<SecurityNav> getSecurityNavsByAuthorities(Set<SecurityAuthority> authorities) {

        Set<SecurityNav> navs = new HashSet<SecurityNav>();

        for (SecurityAuthority authority : authorities) {
            // get has relationship authority
            authority = authorityCache.get(authority.getId());
            for (SecurityResourceEntity resourceEntity : authority.getResources()) {
                SecurityNav nav = navCache.get(resourceEntity.getId());
                if (nav != null) {
                    navs.add(nav);
                }
            }
        }

        return navs;
    }

    public Set<SecurityRole> getRolesByArrayNames(String[] roleNames) {
        Set<SecurityRole> roles = new HashSet<SecurityRole>();

        Set<Long> keys = roleCache.keySet();

        for (Long key : keys) {
            SecurityRole role = roleCache.get(key);

            for (String roleName: roleNames) {
                if (role.getName().equals(roleName)) {
                    roles.add(role);
                }
            }
        }

        return roles;
    }

    public Set<SecurityGroup> getGroupsByArrayNames(String[] groupNames) {
        Set<SecurityGroup> groups = new HashSet<SecurityGroup>();

        Set<Long> keys = groupCache.keySet();

        for (Long key : keys) {
            SecurityGroup group = groupCache.get(key);

            for (String groupName: groupNames) {
                if (group.getName().equals(groupName)) {
                    groups.add(group);
                }
            }
        }

        return groups;
    }

    /**
     * Update resource. when fort server on update resource. cache sync update resource.
     *
     * @param onUpdateSecurityResource the entity
     */
    public void updateResource(OnUpdateSecurityResource onUpdateSecurityResource) throws IOException {
        // get resource class enum
        OnUpdateSecurityResourceClass resourceClass = onUpdateSecurityResource.getResourceClass();
        if (SECURITY_RESOURCE_ENTITY.equals(resourceClass)) {// update resource entity
            updateResourceEntity(onUpdateSecurityResource);
        } else if (SECURITY_NAV.equals(resourceClass)) {// update nav
            updateNav(onUpdateSecurityResource);
        } else if (SECURITY_AUTHORITY.equals(resourceClass)) {// update authority
            updateAuthority(onUpdateSecurityResource);
        } else if (SECURITY_GROUP.equals(resourceClass)) {// update group
            updateGroup(onUpdateSecurityResource);
        } else if (SECURITY_ROLE.equals(resourceClass)) {// update role
            updateRole(onUpdateSecurityResource);
        } else if (SECURITY_USER.equals(resourceClass)) {// update user
            updateUser(onUpdateSecurityResource);
        } else {
            // warning: we don't have this resource class
            log.warn("We don't have this resource class: {}", resourceClass);
        }

        log.info("Updated security resource! {}", onUpdateSecurityResource);
    }

    /**
     * Update user.
     *
     * @param onUpdateSecurityResource the on update bean.
     */
    private void updateUser(OnUpdateSecurityResource onUpdateSecurityResource) throws IOException {
        OnUpdateSecurityResourceOption option = onUpdateSecurityResource.getOption();
        SecurityUser user = mapper.readValue(mapper.writeValueAsString(onUpdateSecurityResource.getData()), SecurityUser.class);

        if (POST.equals(option) || PUT.equals(option)) {
            updateLoggedUserCache(user);
        } else if (DELETE.equals(option)) {
            loggedUserCache.remove(user.getToken());
        }
    }

    /**
     * Update role.
     *
     * @param onUpdateSecurityResource the on update bean.
     */
    private void updateRole(OnUpdateSecurityResource onUpdateSecurityResource) throws IOException {
        OnUpdateSecurityResourceOption option = onUpdateSecurityResource.getOption();
        SecurityRole role = mapper.readValue(mapper.writeValueAsString(onUpdateSecurityResource.getData()), SecurityRole.class);

        if (POST.equals(option) || PUT.equals(option)) {
            roleCache.put(role.getId(), role);
        } else if (DELETE.equals(option)) {
            roleCache.remove(role.getId());
        }
    }

    /**
     * Update group
     *
     * @param onUpdateSecurityResource the on update group.
     */
    private void updateGroup(OnUpdateSecurityResource onUpdateSecurityResource) throws IOException {
        OnUpdateSecurityResourceOption option = onUpdateSecurityResource.getOption();
        SecurityGroup group = mapper.readValue(mapper.writeValueAsString(onUpdateSecurityResource.getData()), SecurityGroup.class);

        if (POST.equals(option) || PUT.equals(option)) {
            groupCache.put(group.getId(), group);
        } else if (DELETE.equals(option)) {
            groupCache.remove(group.getId());
        }
    }

    /**
     * Update authority
     *
     * @param onUpdateSecurityResource the on update bean.
     */
    private void updateAuthority(OnUpdateSecurityResource onUpdateSecurityResource) throws IOException {
        OnUpdateSecurityResourceOption option = onUpdateSecurityResource.getOption();
        SecurityAuthority authority = mapper.readValue(mapper.writeValueAsString(onUpdateSecurityResource.getData()), SecurityAuthority.class);

        if (POST.equals(option) || PUT.equals(option)) {
            authorityCache.put(authority.getId(), authority);
        } else if (DELETE.equals(option)) {
            authorityCache.remove(authority.getId());
        }

        updateResourceAuthoritiesMap(onUpdateSecurityResource);
    }

    /**
     * Update resourceAuthoritiesMap
     *
     * @param onUpdateSecurityResource the on update bean.
     */
    private void updateResourceAuthoritiesMap(OnUpdateSecurityResource onUpdateSecurityResource) throws IOException {
        OnUpdateSecurityResourceOption option = onUpdateSecurityResource.getOption();
        SecurityAuthority authority = mapper.readValue(mapper.writeValueAsString(onUpdateSecurityResource.getData()), SecurityAuthority.class);

        if (POST.equals(option) || PUT.equals(option)) {
            removeAuthorityFromResourceAuthoritiesMap(authority.getId());
            loadResourceAuthoritiesIdsMap(authority);
        } else if (DELETE.equals(option)) {
            removeAuthorityFromResourceAuthoritiesMap(authority.getId());
        }
    }

    /**
     * Remove this authority from resourceAuthoritiesMap
     *
     * @param authorityId the id of the authority
     */
    private void removeAuthorityFromResourceAuthoritiesMap(Long authorityId) {
        for (Long resourceId: resourceAuthoritiesMap.keySet()) {
            resourceAuthoritiesMap.get(resourceId).remove(authorityId);
        }
    }

    /**
     * Update nav.
     *
     * @param onUpdateSecurityResource the on update bean.
     */
    private void updateNav(OnUpdateSecurityResource onUpdateSecurityResource) throws IOException {
        OnUpdateSecurityResourceOption option = onUpdateSecurityResource.getOption();
        SecurityNav nav = mapper.readValue(mapper.writeValueAsString(onUpdateSecurityResource.getData()), SecurityNav.class);

        if (POST.equals(option) || PUT.equals(option)) {
            if (nav.getResource() != null) {
                navCache.put(nav.getResource().getId(), nav);
            }
        } else if (DELETE.equals(option)) {
            if (nav.getResource() != null) {
                navCache.remove(nav.getResource().getId());
            }
        }
    }

    /**
     * Update resource entity.
     *
     * @param onUpdateSecurityResource the on update bean.
     */
    private void updateResourceEntity(OnUpdateSecurityResource onUpdateSecurityResource) throws IOException {
        OnUpdateSecurityResourceOption option = onUpdateSecurityResource.getOption();
        SecurityResourceEntity resource = mapper.readValue(mapper.writeValueAsString(onUpdateSecurityResource.getData()), SecurityResourceEntity.class);

        if (POST.equals(option) || PUT.equals(option)) {
            // update resource entity cache
            resourceEntityCache.put(resource.getId(), resource);

            // update resource url cache
            removeResourceUrlIdMapById(resource.getId());
            resourceUrlIdMap.put(resource.getUrl(), resource.getId());

            // update nav cache
            SecurityNav nav = navCache.get(resource.getId());
            if (nav != null) {
                nav.setResource(resource);
                navCache.put(resource.getId(), nav);
            }
        } else if (DELETE.equals(option)) {
            resourceEntityCache.remove(resource.getId());
            removeResourceUrlIdMapById(resource.getId());
        }
    }

    /**
     * remove resourceUrlIdMap by id, because url id updatable so foreach remove.
     *
     * @param resourceId the id of the SecurityResourceEntity
     */
    private void removeResourceUrlIdMapById(Long resourceId) {
        // remove from resource url id map
        // find key
        String removeKey = null;
        for (String url : resourceUrlIdMap.keySet()) {
            Long id = resourceUrlIdMap.get(url);
            if (id.equals(resourceId)) {
                removeKey = url;
                break;
            }
        }
        // remove if not null
        if (removeKey != null) {
            resourceUrlIdMap.remove(removeKey);
        }
    }

    /**
     * update logged user cache.
     *
     * @param user the user.
     */
    public void updateLoggedUserCache(SecurityUser user) {
        loggedUserCache.put(user.getToken(), user);
    }

    /**
     * get fort context by user.
     *
     * @param user the user
     * @return fort context
     */
    private FortContext getFortContext(SecurityUser user) {
        // building fort context
        FortContext context = FortContext.createEmptyContext();// create empty context

        // set user
        context.setSecurityUser(user);

        // set authorities
        Set<SecurityAuthority> authorities = new HashSet<SecurityAuthority>();
        Set<SecurityRole> roles = user.getRoles();
        for (SecurityRole role : roles) {
            // get full role from cache, this role has eager relationships
            SecurityRole fullRole = getRole(role.getId());
            if (fullRole.getAuthorities() != null) {
                authorities.addAll(fullRole.getAuthorities());
            }
        }
        context.setAuthorities(authorities);

        // set tree navs
        Set<SecurityNav> navs = getSecurityNavsByAuthorities(authorities);
        context.setNavs(TreeSecurityNav.build(navs));

        return context;
    }

    /**
     * get fort context by user token. get first from cache.if cache not found.
     * then get from fort server. if fort server not found return null.
     *
     * @param token the user token
     * @return fort context
     */
    public FortContext getFortContext(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }

        // get first from cache.
        SecurityUser user = loggedUserCache.get(token);
        if (user == null) {
            // get from fort server.
            try {
                user = managerClient.getByUserToken(token);
                if (user == null) {
                    return null;
                } else {
                    // update cache
                    updateLoggedUserCache(user);
                }
            } catch (Exception | FortCrudException e) {
                log.error("Get user by token error! token: {}", token, e.getMessage());
                return null;
            }
        } else if (user.getTokenOverdueTime().before(new Date())){
            // the token is overdue time
            loggedUserCache.remove(token);
            return null;
        }

        return getFortContext(user);
    }
}
