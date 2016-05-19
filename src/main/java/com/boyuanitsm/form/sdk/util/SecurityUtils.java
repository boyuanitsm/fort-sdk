package com.boyuanitsm.form.sdk.util;

import com.boyuanitsm.fort.sdk.context.FortContextHolder;
import com.boyuanitsm.fort.sdk.domain.SecurityGroup;
import com.boyuanitsm.fort.sdk.domain.SecurityRole;
import com.boyuanitsm.fort.sdk.domain.SecurityUser;

import java.util.Set;

/**
 * Utility class for Fort Security.
 *
 * @author zhanghua on 5/19/16.
 */
public final class SecurityUtils {
    private SecurityUtils() {
    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user
     */
    public static String getCurrentUserLogin() {
        SecurityUser user = getSecurityUser();
        return user.getLogin();
    }

    /**
     * If the current user has a specific role.
     *
     * @param roleName the name of the SecurityRole
     * @return true if the current user has the role, false otherwise
     */
    public static boolean isCurrentUserInRole(String roleName) {
        SecurityUser user = getSecurityUser();
        Set<SecurityRole> roleSet = user.getRoles();

        for (SecurityRole role: roleSet) {
            if (role.getName().equals(roleName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the login of the current user roles.
     *
     * @return the login of the current user roles;
     */
    public static Set<SecurityRole> getCurrentUserRoles() {
        SecurityUser user = getSecurityUser();
        return user.getRoles();
    }

    /**
     * Get the login of the current user groups.
     *
     * @return the login of the current user groups;
     */
    public static Set<SecurityGroup> getCurrentUserGroups() {
        SecurityUser user = getSecurityUser();
        return user.getGroups();
    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user;
     */
    private static SecurityUser getSecurityUser() {
        return FortContextHolder.getContext().getSecurityUser();
    }
}
