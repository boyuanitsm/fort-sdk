package com.boyuanitsm.fort.sdk.filter;

import com.boyuanitsm.fort.sdk.cache.FortResourceCache;
import com.boyuanitsm.fort.sdk.client.FortClient;
import com.boyuanitsm.fort.sdk.config.FortConfiguration;
import com.boyuanitsm.fort.sdk.context.FortContext;
import com.boyuanitsm.fort.sdk.context.FortContextHolder;
import com.boyuanitsm.fort.sdk.domain.SecurityAuthority;
import com.boyuanitsm.fort.sdk.domain.SecurityResourceEntity;
import com.boyuanitsm.fort.sdk.domain.SecurityUser;
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
import java.util.Set;

/**
 * Security Http Filter
 *
 * @author zhanghua on 5/17/16.
 */
@Component
public class SecurityHttpFilter implements Filter{

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
         * login handler
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
                FortContext context = FortContext.createEmptyContext();
                context.setSecurityUser(user);
                session.setAttribute(FortContextHolder.FORT_SESSION_NAME, context);

                response.getOutputStream().print("login success");
            } catch (Exception e) {
                // login or password error
                response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            }
        }

        /**
         * logout handler
         *
         * @param request  http servlet request
         * @param response http servlet response
         * @throws IOException
         */
        private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
            HttpSession session = request.getSession();

            session.removeAttribute(FortContextHolder.FORT_SESSION_NAME);

            response.getOutputStream().print("logout success");
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
                // redirect to login view
            }

            // get resource entity
            SecurityResourceEntity resourceEntity = cache.getResourceEntity(resourceId);
            // get this resource relation authorities
            Set<SecurityAuthority> authorities = resourceEntity.getAuthorities();



            chain.doFilter(request, response);
        }
    }

}
