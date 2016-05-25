## fort.yml

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
