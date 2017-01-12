/*
 * Copyright 2016-2017 Shanghai Boyuan IT Services Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.boyuanitsm.fort.sdk.config;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

/**
 * fort configuration. read file fort.yml.
 *
 * @author zhanghua on 5/16/16.
 */
@Component
@SuppressWarnings(value = {"unchecked"})
public class FortProperties {

    private final App app;
    private final ResourceSync resourceSync;
    private final Authentication authentication;
    private final User user;
    private final Cookie cookie;
    private final String[] ignores;

    public FortProperties() throws IOException {
        Yaml yaml = new Yaml();
        InputStream is = FortProperties.class.getClassLoader().getResourceAsStream("fort.yml");
        Map<String, Object> conf = ((Map<String, Map<String, Object>>) yaml.load(is)).get("fort");

        app = new App(conf.get("app"));
        resourceSync = new ResourceSync(conf.get("resource-sync"));
        authentication = new Authentication(conf.get("authentication"));
        user = new User(conf.get("user"));
        cookie = new Cookie(conf.get("cookie"));

        ArrayList<Object> ignoreList = (ArrayList<Object>) conf.get("ignores");
        ignores = new String[ignoreList.size()];
        for (int i = 0; i < ignoreList.size(); i++) {
            ignores[i] = String.valueOf(ignoreList.get(i));
        }
    }

    public static class App {
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

    public static class ResourceSync {
        ResourceSync(Object resourceSync) {
            Map<String, Object> resourceSyncMap = (Map<String, Object>) resourceSync;
            this.enable = Boolean.valueOf(String.valueOf(resourceSyncMap.get("enable")));
        }

        private final boolean enable;

        public boolean isEnable() {
            return enable;
        }
    }

    public static class Authentication {
        Authentication(Object authentication) {
            Map<String, Object> authenticationMap = (Map<String, Object>) authentication;
            this.unauthorizedReturn = String.valueOf(authenticationMap.get("unauthorized-return"));
            this.login = new Login(authenticationMap.get("login"));
            this.logout = new Logout(authenticationMap.get("logout"));
        }

        private final String unauthorizedReturn;
        private final Login login;
        private final Logout logout;

        public static class Login {
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

        public static class Logout {
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

    public static class User {
        User(Object user) {
            Map<String, ArrayList<Object>> userMap = (Map<String, ArrayList<Object>>) user;
            ArrayList<Object> defaultRoles = userMap.get("default-roles");
            ArrayList<Object> defaultGroups = userMap.get("default-groups");

            this.defaultRoles = new String[defaultRoles.size()];
            this.defaultGroups = new String[defaultGroups.size()];

            for (int i = 0; i < defaultRoles.size(); i++) {
                this.defaultRoles[i] = String.valueOf(defaultRoles.get(i));
            }

            for (int i = 0; i < defaultGroups.size(); i++) {
                this.defaultGroups[i] = String.valueOf(defaultGroups.get(i));
            }
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

    public static class Cookie {
        Cookie(Object cookie) {
            Map<String, Object> cookieMap = (Map<String, Object>) cookie;

            this.domain = cookieMap.get("domain") == null ? null : String.valueOf(cookieMap.get("domain"));
            // max-age unit day transform second
            this.maxAge = (int) (Float.valueOf(String.valueOf(cookieMap.get("max-age"))) * (60 * 60 * 24));
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

    public ResourceSync getResourceSync() {
        return resourceSync;
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

    public String[] getIgnores() {
        return ignores;
    }
}
