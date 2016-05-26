com.boyuanitsm.fort.sdk.util
## Class SecurityUtils

可以获得当前登录用户所有的安全信息。

### Constructor Summary

#### Constructors

| Modifier	| Constructor and Description |
| --------- | --------------------------- |
| private	| SecurityUtils()             |

### Method Summary

#### Methods

 static java.util.Set<SecurityAuthority>   | getCurrentUserAuthorities()
 ------------------------------------------| -----------------------------
 static java.util.Set<SecurityGroup>	   | getCurrentUserGroups()        
 static java.lang.String                   | getCurrentUserLogin()     
 static java.util.Set<SecurityRole>        | getCurrentUserRoles()
 static java.util.List<TreeSecurityNav>    | getCurrentUserTreeSecurityNavs()
 static boolean                            | isCurrentUserInRole(java.lang.String roleName)

### Method Detail

#### getCurrentUserLogin

```
public static java.lang.String getCurrentUserLogin()
Get the login of the current user.
Returns:
    the login of the current user
```
#### isCurrentUserInRole

```
public static boolean isCurrentUserInRole(java.lang.String roleName)
If the current user has a specific role.
Parameters:
    roleName - the name of the SecurityRole
Returns:
    true if the current user has the role, false otherwise
```

#### getCurrentUserRoles

```
public static java.util.Set<SecurityRole> getCurrentUserRoles()
Get the login of the current user roles.
Returns:
    the login of the current user roles.
```

#### getCurrentUserGroups

```
public static java.util.Set<SecurityGroup> getCurrentUserGroups()
Get the login of the current user groups.
Returns:
    the login of the current user groups.
```

#### getCurrentUserAuthorities

```
public static java.util.Set<SecurityAuthority> getCurrentUserAuthorities()
Get the login of the current user authorities.
Returns:
    the login of the current user authorities.
```

#### getCurrentUserTreeSecurityNavs

```
public static java.util.List<TreeSecurityNav> getCurrentUserTreeSecurityNavs()
Get the login of the current user tree security navs.
Returns:
    the login of the current user tree security navs.
```
