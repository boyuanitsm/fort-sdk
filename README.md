# fort sdk

## 目录

* 开始
    * 依赖
        * 完整 maven pom
    * 配置 fort.yml
    * 配置 fort 拦截器
    * 启动 spring注解扫描
* 使用
    * FortClient
    * SecurityUtils

## 开始

### 依赖

*   jdk 7+
*   spring 4+
*   apache http client 4.5.2
*   servlet api 3+
*   spring websocket 4.2.5.RELEASE
*   alibaba fastjson 1.2.7
*   snakeyaml 1.6

#### 完整 maven pom

```
<!-- begin fort sdk depend -->
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.5.2</version>
</dependency>
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>3.1.0</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-websocket</artifactId>
    <version>4.2.5.RELEASE</version>
</dependency>
<dependency>
    <groupId>javax.websocket</groupId>
    <artifactId>javax.websocket-api</artifactId>
    <version>1.1</version>
</dependency>
<dependency>
    <groupId>org.glassfish.tyrus</groupId>
    <artifactId>tyrus-container-grizzly-client</artifactId>
    <version>1.8.3</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-messaging</artifactId>
    <version>4.2.5.RELEASE</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.5.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.7</version>
</dependency>
<dependency>
    <groupId>org.yaml</groupId>
    <artifactId>snakeyaml</artifactId>
    <version>1.6</version>
</dependency>
<!-- end fort sdk depend -->
```

### 配置 fort.yml

```
fort:
    app:
        server-base: http://localhost:8080
        app-key: soqcnpjhxqku
        app-secret: 6ddeh9nrgtjd
    authentication:
        unauthorized-return: /unauthorized.html
        login:
            url: /fort/login
            success-return: /index.html
            error-return: /login.html?error=1
            login-view: /login.html
        logout:
            url: /fort/logout
            success-return: /index.html
    user:
        default-roles: user
        default-groups: dev
```

### 配置 fort 拦截器

在web.xml加入：

```
<filter>   
    <filter-name>fortSecurityHttpFilter</filter-name>   
    <filter-class>com.boyuanitsm.fort.sdk.filter.SecurityHttpFilter</filter-class>   
</filter>   
<filter-mapping>   
    <filter-name>fortSecurityHttpFilter</filter-name>   
    <url-pattern>/*</url-pattern>   
</filter-mapping>
```

### 启动 spring 注解扫描

在spring application.xml加入：

```
<context:component-scan base-package="com.boyuanitsm.fort.sdk"/>
```
