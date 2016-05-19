package com.boyuanitsm.fort.sdk.filter;

import com.boyuanitsm.fort.sdk.cache.FortResourceCache;
import com.boyuanitsm.fort.sdk.client.FortClient;
import com.boyuanitsm.fort.sdk.config.FortConfiguration;
import com.boyuanitsm.fort.sdk.context.FortContext;
import com.boyuanitsm.fort.sdk.context.FortContextHolder;
import com.boyuanitsm.fort.sdk.domain.SecurityAuthority;
import com.boyuanitsm.fort.sdk.domain.SecurityResourceEntity;
import com.boyuanitsm.fort.sdk.domain.SecurityRole;
import com.boyuanitsm.fort.sdk.domain.SecurityUser;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Security Http Filter
 *
 * @author zhanghua on 5/17/16.
 */
@Component
public class SecurityHttpFilter implements Filter {

    private final Logger log = LoggerFactory.getLogger(SecurityHttpFilter.class);

    @Autowired
    private FortConfiguration configuration;

    @Autowired
    private FortClient client;

    @Autowired
    private FortResourceCache cache;

    private AuthenticationHandler handler = new AuthenticationHandler();

    public void init(FilterConfig filterConfig) throws ServletException {
        // on init
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestUri = request.getRequestURI();

        log.debug("request uri: {}", requestUri);

        if (configuration.getLogin().getUrl().equals(requestUri)) {
            handler.login(request, response);
            return;
        } else if (configuration.getLogout().getUrl().equals(requestUri)) {
            handler.logout(request, response);
            return;
        } else {
            Long resourceId = cache.getResourceId(requestUri);
            if (resourceId != null) {
                handler.authentication(request, response, chain, resourceId);
                return;
            }
            // handler.setFortContext(request);
        }

        chain.doFilter(request, response);
    }

    public void destroy() {
        // on destroy
    }

    /**
     * Fort Authentication Handler.
     *
     * @author zhanghua on 5/17/16.
     */
    private class AuthenticationHandler {

        /**
         * login handler. login is form parameter f_username. password is form parameter f_password.
         *
         * @param request  http servlet request
         * @param response http servlet response
         * @throws IOException
         */
        private void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
            String login = request.getParameter("f_username");
            String password = request.getParameter("f_password");
            try {
                SecurityUser user = client.authorization(login, password);

                HttpSession session = request.getSession();
                // create empty context
                FortContext context = FortContext.createEmptyContext();

                // set user
                context.setSecurityUser(user);

                // set authorities
                Set<SecurityAuthority> authorities = new HashSet<SecurityAuthority>();
                Set<SecurityRole> roles = user.getRoles();
                for (SecurityRole role : roles) {
                    authorities.addAll(role.getAuthorities());
                }
                context.setAuthorities(authorities);

                // set session attribute
                session.setAttribute(FortContextHolder.FORT_SESSION_NAME, context);

                // login success, redirect to success return
                response.sendRedirect(configuration.getLogin().getSuccessReturn());
            } catch (Exception e) {
                // login or password error, redirect to error return
                response.sendRedirect(configuration.getLogin().getErrorReturn());
            }
        }

        /**
         * logout handler. remove session attribute FortContextHolder.FORT_SESSION_NAME.
         *
         * @param request  http servlet request
         * @param response http servlet response
         * @throws IOException
         */
        private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
            HttpSession session = request.getSession();
            session.removeAttribute(FortContextHolder.FORT_SESSION_NAME);
            response.sendRedirect(configuration.getLogout().getSuccessReturn());
        }

        /**
         * set fort content. get session attribute , session name FortContextHolder.FORT_SESSION_NAME
         *
         * @param request http servlet request
         */
        private void setFortContext(HttpServletRequest request) {
            Object attr = request.getSession().getAttribute(FortContextHolder.FORT_SESSION_NAME);
            if (attr != null) {
                FortContextHolder.setContext((FortContext) attr);
            } else {
                FortContextHolder.setContext(FortContext.createEmptyContext());
            }
        }

        private void authentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Long resourceId) throws IOException, ServletException {
            // setFortContext(request);
            Object attr = request.getSession().getAttribute(FortContextHolder.FORT_SESSION_NAME);

            if (attr == null) {
                // no logged, redirect to login view
                response.sendRedirect(configuration.getLogin().getLoginView());
                return;
            }

            FortContext context = (FortContext) attr;
            // get resource entity
            SecurityResourceEntity resourceEntity = cache.getResourceEntity(resourceId);
            // get this resource relation authorities
            Set<SecurityAuthority> authorities = resourceEntity.getAuthorities();
            // get user authorities
            Set<SecurityAuthority> userAuthorities = context.getAuthorities();

            boolean isAllow = false;
            for (SecurityAuthority authority: authorities) {
                for (SecurityAuthority userAuthority: userAuthorities) {
                    if (authority.getId().equals(userAuthority.getId())) {
                        isAllow = true;
                        break;
                    }
                }

                if (isAllow) {
                    break;
                }
            }

            if (isAllow) {// ok
                chain.doFilter(request, response);
            } else {// un authorized
                response.sendRedirect(configuration.getAuthentication().getUnauthorizedReturn());
            }
        }
    }
}
