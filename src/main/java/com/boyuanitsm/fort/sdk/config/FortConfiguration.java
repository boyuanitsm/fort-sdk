package com.boyuanitsm.fort.sdk.config;

import com.boyuanitsm.fort.sdk.cache.FortResourceCache;
import com.boyuanitsm.fort.sdk.domain.SecurityGroup;
import com.boyuanitsm.fort.sdk.domain.SecurityRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * fort configuration. read file fort.yml.
 *
 * @author zhanghua on 5/16/16.
 */
@Component
public class FortConfiguration {

    private final Logger log = LoggerFactory.getLogger(FortConfiguration.class);

    private final App app;
    private final Authentication authentication;
    private final User user;

    public FortConfiguration() {
        Yaml yaml = new Yaml();
        InputStream is = FortConfiguration.class.getClassLoader().getResourceAsStream("fort.yml");
        Map<String, Object> conf = ((Map<String, Map<String, Object>>) yaml.load(is)).get("fort");
        app = new App(conf.get("app"));
        authentication = new Authentication(conf.get("authentication"));
        user = new User(conf.get("user"));
    }

    public class App {
        App(Object app) {
            Map<String, String> appMap = (Map<String, String>) app;
            this.serverBase = appMap.get("serverBase");
            this.appKey = appMap.get("appKey");
            this.appSecret = appMap.get("appSecret");
        }

        private String serverBase;
        private String appKey;
        private String appSecret;

        public String getServerBase() {
            return serverBase;
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
                    ", appKey='" + appKey + '\'' +
                    ", appSecret='" + appSecret + '\'' +
                    '}';
        }
    }

    public class Authentication {
        Authentication(Object authentication) {
            Map<String, String> authenticationMap = (Map<String, String>) authentication;
            this.loginProcessingUrl = authenticationMap.get("loginProcessingUrl");
            this.logoutUrl = authenticationMap.get("logoutUrl");
        }

        private String loginProcessingUrl;
        private String logoutUrl;

        public String getLoginProcessingUrl() {
            return loginProcessingUrl;
        }

        public String getLogoutUrl() {
            return logoutUrl;
        }

        @Override
        public String toString() {
            return "Authentication{" +
                    "loginProcessingUrl='" + loginProcessingUrl + '\'' +
                    ", logoutUrl='" + logoutUrl + '\'' +
                    '}';
        }
    }

    public class User {
        User(Object user) {
            Map<String, String> userMap = (Map<String, String>) user;
            String defaultRoles = userMap.get("defaultRoles");
            String defaultGroups = userMap.get("defaultGroups");

            String[] roles = defaultRoles.split(",");
            String[] groups = defaultGroups.split(",");

            this.defaultRoles = new HashSet<SecurityRole>();
            this.defaultGroups = new HashSet<SecurityGroup>();

//            for (String name: roles) {
//                name = name.trim();
//                SecurityRole role = cache.getSecurityRoleByName(name);
//                if (role == null) {
//                    log.warn("user default role not found! name: {}", name);
//                    continue;
//                }
//                this.defaultRoles.add(role);
//            }
//            for (String name: groups) {
//                name = name.trim();
//                SecurityGroup group = cache.getSecurityGroupByName(name);
//                if (group == null) {
//                    log.warn("user default group not found! name: {}", name);
//                    continue;
//                }
//                this.defaultGroups.add(group);
//            }
        }

        private Set<SecurityRole> defaultRoles;
        private Set<SecurityGroup> defaultGroups;

        public Set<SecurityRole> getDefaultRoles() {
            return defaultRoles;
        }

        public void setDefaultRoles(Set<SecurityRole> defaultRoles) {
            this.defaultRoles = defaultRoles;
        }

        public Set<SecurityGroup> getDefaultGroups() {
            return defaultGroups;
        }

        public void setDefaultGroups(Set<SecurityGroup> defaultGroups) {
            this.defaultGroups = defaultGroups;
        }

        @Override
        public String toString() {
            return "User{" +
                    "defaultRoles=" + defaultRoles +
                    ", defaultGroups=" + defaultGroups +
                    '}';
        }
    }

    public App getApp() {
        return app;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public User getUser() {
        return user;
    }
}
