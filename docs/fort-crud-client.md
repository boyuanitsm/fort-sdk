## Fort Crud Client

这个客户端提供了与server间所有的增删改查功能。

### 注册
```
@Autowired
private FortCrudClient crudClient;

@RequestMapping("/api/signup")
void signup(SecurityUser user, HttpServletResponse response) throws IOException, HttpException {
    crudClient.signUp(user);
    response.sendRedirect("/login.html");
}
```

| 字段 | 验证 | 描述 |
| ------------ | ------------- | ------------ |
| login | required maxlength(50)  | 用户名 |
| passwordHash | required  | 密码 |

### 重置密码
```
@Autowired
private FortCrudClient crudClient;

@RequestMapping("/api/resetPassword")
void signup(String newPassword) throws IOException, HttpException {
    Long userId = SecurityUtils.getCurrentUserId();
    crudClient.resetPassword(userId, newPassword);
    response.sendRedirect("/login.html");
}
```

| 字段 | 验证 | 描述 |
| ------------ | ------------- | ------------ |
| newPassword | required | 新密码 |
