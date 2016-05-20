package com.boyuanitsm.fort.sdk.cache;

import com.boyuanitsm.fort.sdk.client.FortClient;
import com.boyuanitsm.fort.sdk.domain.*;
import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * fort resource cache.
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
}
