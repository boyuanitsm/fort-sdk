package com.boyuanitsm.fort.sdk.config;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * fort configuration. read file fort.yml.
 *
 * @author zhanghua on 5/16/16.
 */
@Component
@SuppressWarnings(value = {"unchecked"})
public class FortConfiguration {

    private final App app;
    private final Authentication authentication;
    private final User user;
    private final Cookie cookie;

    public FortConfiguration() throws IOException {
        Yaml yaml = new Yaml();
        InputStream is = FortConfiguration.class.getClassLoader().getResourceAsStream("fort.yml");
        Map<String, Object> conf = ((Map<String, Map<String, Object>>) yaml.load(is)).get("fort");
        app = new App(conf.get("app"));
        authentication = new Authentication(conf.get("authentication"));
        user = new User(conf.get("user"));
        cookie = new Cookie(conf.get("cookie"));
    }

    public class App {
        App(Object app) {
            Map<String, String> appMap = (Map<String, String>) app;
            this.serverBase = appMap.get("server-base");
            this.appKey = appMap.get("app-key");
            this.appSecret = appMap.get("app-secret");

            this.websocketServerBase = "ws" + serverBase.substring(serverBase.indexOf(":"), serverBase.length());
        }

        private final String serverBase;
        private final String websocketServerBase;
        private final String appKey;
        private final String appSecret;

        public String getServerBase() {
            return serverBase;
        }

        public String getWebsocketServerBase() {
            return websocketServerBase;
        }

        public String getAppKey() {
            return appKey;
        }

        public String getAppSecret() {
            return appSecret;
        }

        @Override
        public String toString() {
            return "App{" +
                    "serverBase='" + serverBase + '\'' +
                    ", websocketServerBase='" + websocketServerBase + '\'' +
                    ", appKey='" + appKey + '\'' +
                    ", appSecret='" + appSecret + '\'' +
                    '}';
        }
    }

    public class Authentication {
        Authentication(Object authentication) {
            Map<String, Object> authenticationMap = (Map<String, Object>) authentication;
            this.unauthorizedReturn = String.valueOf(authenticationMap.get("unauthorized-return"));
            this.login = new Login(authenticationMap.get("login"));
            this.logout = new Logout(authenticationMap.get("logout"));
        }

        private final String unauthorizedReturn;
        private final Login login;
        private final Logout logout;

        public class Login {
            Login(Object login) {
                Map<String, String> loginMap = (Map<String, String>) login;
                this.url = loginMap.get("url");
                this.successReturn = loginMap.get("success-return");
                this.errorReturn = loginMap.get("error-return");
                this.loginView = loginMap.get("login-view");
            }

            private final String url;
            private final String successReturn;
            private final String errorReturn;
            private final String loginView;

            public String getUrl() {
                return url;
            }

            public String getSuccessReturn() {
                return successReturn;
            }

            public String getErrorReturn() {
                return errorReturn;
            }

            public String getLoginView() {
                return loginView;
            }
        }

        public class Logout {
            Logout(Object logout) {
                Map<String, String> loginMap = (Map<String, String>) logout;
                this.url = loginMap.get("url");
                this.successReturn = loginMap.get("success-return");
            }

            private final String url;
            private final String successReturn;

            public String getUrl() {
                return url;
            }

            public String getSuccessReturn() {
                return successReturn;
            }
        }

        public Login getLogin() {
            return login;
        }

        public Logout getLogout() {
            return logout;
        }

        public String getUnauthorizedReturn() {
            return unauthorizedReturn;
        }
    }

    public class User {
        User(Object user) {
            Map<String, String> userMap = (Map<String, String>) user;
            String defaultRoles = userMap.get("default-roles");
            String defaultGroups = userMap.get("default-groups");

            this.defaultRoles = defaultRoles.split(",");
            this.defaultGroups = defaultGroups.split(",");
        }

        private final String[] defaultRoles;
        private final String[] defaultGroups;

        public String[] getDefaultRoles() {
            return defaultRoles;
        }

        public String[] getDefaultGroups() {
            return defaultGroups;
        }
    }

    public class Cookie {
        Cookie(Object cookie) {
            Map<String, Object> cookieMap = (Map<String, Object>) cookie;

            this.domain = String.valueOf(cookieMap.get("domain"));
            this.maxAge = Integer.valueOf(String.valueOf(cookieMap.get("max-age")));
        }

        private final String domain;
        private final int maxAge;

        public String getDomain() {
            return domain;
        }

        public int getMaxAge() {
            return maxAge;
        }
    }

    public App getApp() {
        return app;
    }

    public User getUser() {
        return user;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public Authentication.Login getLogin() {
        return authentication.login;
    }

    public Authentication.Logout getLogout() {
        return authentication.logout;
    }

    public Cookie getCookie() {
        return cookie;
    }
}
