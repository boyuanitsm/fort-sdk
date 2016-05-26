com.boyuanitsm.fort.sdk.client

## Class FortCrudClient

这个客户端提供了与server间所有的增删改查功能。

### Constructor
```
@Autowired
private FortCrudClient crudClient;
```
### Method Summary

 Modifier and Type             | Method and Description
 ----------------------------- | -----------------------------
 SecurityGroup                 | createSecurityGroup(SecurityGroup securityGroup)
 void	                       | deleteSecurityGroup(java.lang.Long id)
 java.util.List<SecurityGroup> | getAllSecurityGroup()
 SecurityGroup                 | getSecurityGroup(java.lang.Long id)
 void                          | signUp(SecurityUser user)
 SecurityGroup                 | updateSecurityGroup(SecurityGroup securityGroup)

### Method Detail

#### createSecurityGroup

```
public SecurityGroup createSecurityGroup(SecurityGroup securityGroup)
                                  throws FortCrudException
POST /security-groups : Create a new securityGroup.
Parameters:
    securityGroup - the securityGroup to create
Returns:
    the ResponseEntity with status 201 (Created) and with body the new securityGroup, or with throw FortNoValidException 400 (Bad Request) if the securityGroup has already an ID
Throws:
    FortCrudException
```

#### deleteSecurityGroup

```
public void deleteSecurityGroup(java.lang.Long id)
                         throws FortCrudException
DELETE /security-groups/:id : delete the "id" securityGroup.
Parameters:
    id - the id of the securityGroup to delete
Throws:
    FortCrudException
```

#### getAllSecurityGroup

```
public java.util.List<SecurityGroup> getAllSecurityGroup()
Get all the securityGroups from cache.
```

#### getSecurityGroup

```
public SecurityGroup getSecurityGroup(java.lang.Long id)
Get the "id" securityGroup
Parameters:
    id - the id of the securityGroup to retrieve
Returns:
    the securityGroup, or null (Not Found)
```

#### signUp

```
public void signUp(SecurityUser user)
            throws FortCrudException
Register a new user, before register, set default role,group. role is fort.yml user: defaultRole. multi value comma split. group is fort.yml user: defaultGroup. multi value comma split.
Parameters:
    user - security user, login, passwordHash required
Throws:
    FortCrudException
```

#### updateSecurityGroup

```
public SecurityGroup updateSecurityGroup(SecurityGroup securityGroup)
                                  throws FortCrudException
PUT /security-groups : Updates an existing securityGroup.
Parameters:
    securityGroup - the securityGroup to update
Returns:
    the ResponseEntity with status 200 (OK) and with body the updated securityGroup, or throw FortNoValidException if the securityGroup is not valid, or throw FortCrudException (Internal Server Error) if the securityGroup couldnt be updated
Throws:
    FortCrudException - if the Location URI syntax is incorrect
```
