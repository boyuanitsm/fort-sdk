## Fort SDK

## 目录

* 开始
    * 依赖
        * 完整 maven pom
    * 配置 fort.yml
    * 配置 fort 拦截器
    * 启动 spring注解扫描
* 使用
    * FortCrudClient
        * 注册
        * 重置密码
    * SecurityUtils

### 开始集成Fort SDK

*   [Requirements](docs/requirements.md)
*   [fort.yml](docs/fort-yml.md)
*   [Fort security http filter](docs/fort-filter.md)
*   [Spring component scan](docs/spring-component-scan.md)

## 使用

在项目启动时，会根据配置的server-base、app-key、app-secret连接到`fort server`. 并加载这个应用的全部资源（不包括用户），并启动web socket连接，及时接收服务器更新的资源。

### FortCrudClient

这个客户端提供了与server间所有的增删改查功能。

##### 注册
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

##### 重置密码
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
