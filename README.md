## Fort SDK

![Build Status](http://180.167.77.58:30000/fort/fort-sdk/badges/master/build.svg)

### 开始集成Fort SDK

*   [Requirements](docs/usage/requirements.md)
*   [fort.yml](docs/usage/fort-yml.md)
*   [Fort security http filter](docs/usage/fort-filter.md)
*   [Spring component scan](docs/usage/spring-component-scan.md)

## 使用Fort SDK

在项目启动时，会根据配置的server-base、app-key、app-secret连接到`fort server`. 并加载这个应用的全部资源（不包括用户），并启动web socket连接，及时接收服务器更新的资源。

*   [Fort Crud Client](docs/usage/fort-crud-client.md)
*   [Security Utils](docs/usage/security-utils.md)

## 实体关系图

![domain-jdl](docs/images/jhipster-jdl.png)
