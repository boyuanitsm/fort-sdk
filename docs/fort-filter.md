## Fort security http filter

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
