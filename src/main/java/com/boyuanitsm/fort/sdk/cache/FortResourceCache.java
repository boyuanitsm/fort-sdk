package com.boyuanitsm.fort.sdk.cache;

import com.alibaba.fastjson.JSON;
import com.boyuanitsm.fort.sdk.bean.OnUpdateSecurityResource;
import com.boyuanitsm.fort.sdk.bean.enumeration.OnUpdateSecurityResourceClass;
import com.boyuanitsm.fort.sdk.bean.enumeration.OnUpdateSecurityResourceOption;
import com.boyuanitsm.fort.sdk.client.FortClient;
import com.boyuanitsm.fort.sdk.domain.*;
import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

import static com.boyuanitsm.fort.sdk.bean.enumeration.OnUpdateSecurityResourceClass.*;
import static com.boyuanitsm.fort.sdk.bean.enumeration.OnUpdateSecurityResourceOption.DELETE;
import static com.boyuanitsm.fort.sdk.bean.enumeration.OnUpdateSecurityResourceOption.POST;
import static com.boyuanitsm.fort.sdk.bean.enumeration.OnUpdateSecurityResourceOption.PUT;

/**
 * fort resource cache. cache resource entity, nav, authority, role, group.
 *
 * @author zhanghua on 5/17/16.
 */
@Component
public class FortResourceCache {

    private final Logger log = LoggerFactory.getLogger(FortResourceCache.class);

    private FortClient fortClient;

    /**
     * resource entity cache. SecurityResourceEntity.id - SecurityResourceEntity
     */
    private static Map<Long, SecurityResourceEntity> resourceEntityCache;
    /**
     * resource url id map.  SecurityResourceEntity.url - SecurityResourceEntity.id.
     */
    private static Map<String, Long> resourceUrlIdMap;
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

    @Autowired
    public FortResourceCache(FortClient fortClient) throws IOException, HttpException {
        this.fortClient = fortClient;// autowired fort client
        load();// load resource
    }

    /**
     * init cache. new HashMap.
     */
    private void initCache() {
        resourceEntityCache = new HashMap<Long, SecurityResourceEntity>();
        resourceUrlIdMap = new HashMap<String, Long>();
        navCache = new HashMap<Long, SecurityNav>();
        authorityCache = new HashMap<Long, SecurityAuthority>();
        roleCache = new HashMap<Long, SecurityRole>();
        groupCache = new HashMap<Long, SecurityGroup>();
    }

    /**
     * load resource from fort server.
     *
     * @throws IOException
     * @throws HttpException
     */
    private void load() throws IOException, HttpException{
        log.debug("Starting Fort");
        long t1 = System.currentTimeMillis();

        initCache();

        // load resource entities
        List<SecurityResourceEntity> resourceEntities = fortClient.getAllResourceEntities();
        for (SecurityResourceEntity resourceEntity: resourceEntities) {
            resourceEntityCache.put(resourceEntity.getId(), resourceEntity);
            resourceUrlIdMap.put(resourceEntity.getUrl(), resourceEntity.getId());
        }
        // load navs
        List<SecurityNav> navs = fortClient.getAllSecurityNavs();
        for (SecurityNav nav: navs) {
            navCache.put(nav.getResource().getId(), nav);
        }
        // load authorities
        List<SecurityAuthority> authorities = fortClient.getAllAuthorities();
        for (SecurityAuthority authority: authorities) {
            authorityCache.put(authority.getId(), authority);
        }
        // load roles
        List<SecurityRole> roles = fortClient.getAllRoles();
        for (SecurityRole role: roles) {
            roleCache.put(role.getId(), role);
        }
        // load group
        List<SecurityGroup> groups = fortClient.getAllGroups();
        for (SecurityGroup group: groups) {
            groupCache.put(group.getId(), group);
        }

        log.debug("Started Fort in {} ms", System.currentTimeMillis() - t1);
    }

    /**
     * get security role by name.
     *
     * @param name role name
     * @return if not found return null
     */
    public SecurityRole getSecurityRoleByName(String name) {
        Iterator<Long> keys = roleCache.keySet().iterator();

        while (keys.hasNext()) {
            SecurityRole role = roleCache.get(keys.next());
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
        Iterator<Long> keys = groupCache.keySet().iterator();

        while (keys.hasNext()) {
            SecurityGroup group = groupCache.get(keys.next());
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

        for (SecurityAuthority authority: authorities) {
            // get has relationship authority
            authority = authorityCache.get(authority.getId());
            for (SecurityResourceEntity resourceEntity: authority.getResources()) {
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

        for (Long key: keys) {
            SecurityRole role = roleCache.get(key);

            for (int i = 0; i < roleNames.length; i++) {
                if (role.getName().equals(roleNames[i])) {
                    roles.add(role);
                }
            }
        }

        return roles;
    }

    public Set<SecurityGroup> getGroupsByArrayNames(String[] groupNames) {
        Set<SecurityGroup> groups = new HashSet<SecurityGroup>();

        Set<Long> keys = groupCache.keySet();

        for (Long key: keys) {
            SecurityGroup group = groupCache.get(key);

            for (int i = 0; i < groupNames.length; i++) {
                if (group.getName().equals(groupNames[i])) {
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
    public void updateResource(OnUpdateSecurityResource onUpdateSecurityResource) {
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
        } else {
            // warning: we don't have this resource class
            log.warn("We don't have this resource class: {}", resourceClass);
        }
    }

    /**
     * Update role.
     *
     * @param onUpdateSecurityResource the on update bean.
     */
    private void updateRole(OnUpdateSecurityResource onUpdateSecurityResource) {
        OnUpdateSecurityResourceOption option = onUpdateSecurityResource.getOption();
        SecurityRole role = JSON.toJavaObject((JSON) onUpdateSecurityResource.getData(), SecurityRole.class);

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
    private void updateGroup(OnUpdateSecurityResource onUpdateSecurityResource) {
        OnUpdateSecurityResourceOption option = onUpdateSecurityResource.getOption();
        SecurityGroup group = JSON.toJavaObject((JSON) onUpdateSecurityResource.getData(), SecurityGroup.class);

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
    private void updateAuthority(OnUpdateSecurityResource onUpdateSecurityResource) {
        OnUpdateSecurityResourceOption option = onUpdateSecurityResource.getOption();
        SecurityAuthority authority = JSON.toJavaObject((JSON) onUpdateSecurityResource.getData(), SecurityAuthority.class);

        if (POST.equals(option) || PUT.equals(option)) {
            authorityCache.put(authority.getId(), authority);
        } else if (DELETE.equals(option)) {
            authorityCache.remove(authority.getId());
        }
    }

    /**
     * Update nav.
     *
     * @param onUpdateSecurityResource the on update bean.
     */
    private void updateNav(OnUpdateSecurityResource onUpdateSecurityResource) {
        OnUpdateSecurityResourceOption option = onUpdateSecurityResource.getOption();
        SecurityNav nav = JSON.toJavaObject((JSON) onUpdateSecurityResource.getData(), SecurityNav.class);

        if (POST.equals(option) || PUT.equals(option)) {
            navCache.put(nav.getResource().getId(), nav);
        } else if (DELETE.equals(option)) {
            navCache.remove(nav.getResource().getId());
        }
    }

    /**
     * Update resource entity.
     *
     * @param onUpdateSecurityResource the on update bean.
     */
    private void updateResourceEntity(OnUpdateSecurityResource onUpdateSecurityResource) {
        OnUpdateSecurityResourceOption option = onUpdateSecurityResource.getOption();
        SecurityResourceEntity resource = JSON.toJavaObject((JSON) onUpdateSecurityResource.getData(), SecurityResourceEntity.class);

        if (POST.equals(option) || PUT.equals(option)) {
            resourceEntityCache.put(resource.getId(), resource);
            removeResourceUrlIdMapById(resource.getId());
            resourceUrlIdMap.put(resource.getUrl(), resource.getId());
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
        for (String url: resourceUrlIdMap.keySet()) {
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
}
