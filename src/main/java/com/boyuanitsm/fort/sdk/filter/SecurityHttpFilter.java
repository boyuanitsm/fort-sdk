package com.boyuanitsm.fort.sdk.filter;

import com.boyuanitsm.fort.sdk.cache.FortResourceCache;
import com.boyuanitsm.fort.sdk.client.FortClient;
import com.boyuanitsm.fort.sdk.config.FortConfiguration;
import com.boyuanitsm.fort.sdk.context.FortContext;
import com.boyuanitsm.fort.sdk.domain.*;
import com.boyuanitsm.fort.sdk.exception.FortAuthenticationException;
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
import java.util.Set;

import static com.boyuanitsm.fort.sdk.config.Constants.*;
import static com.boyuanitsm.fort.sdk.context.FortContextHolder.setContext;

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

        handler.setFortContext(request);

        if (configuration.getLogin().getUrl().equals(requestUri)) {
            handler.signIn(request, response);
            return;
        } else if (configuration.getLogout().getUrl().equals(requestUri)) {
            handler.logout(response);
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
         * signIn handler. signIn is form parameter f_username. password is form parameter f_password.
         *
         * @param request  http servlet request
         * @param response http servlet response
         * @throws IOException
         */
        private void signIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
            String login = request.getParameter(LOGIN_FORM_USERNAME_PARAM_NAME);
            String password = request.getParameter(LOGIN_FORM_PASSWORD_PARAM_NAME);
            try {
                SecurityUser user = client.signIn(login, password, request.getRemoteAddr(), request.getHeader(USERAGENT));

                HttpSession session = request.getSession();
                // create empty context
                FortContext context = FortContext.createEmptyContext();

                // set user
                context.setSecurityUser(user);

                // set authorities
                Set<SecurityAuthority> authorities = new HashSet<SecurityAuthority>();
                Set<SecurityRole> roles = user.getRoles();
                for (SecurityRole role : roles) {
                    // get full role from cache, this role has eager relationships
                    SecurityRole fullRole = cache.getRole(role.getId());
                    authorities.addAll(fullRole.getAuthorities());
                }
                context.setAuthorities(authorities);

                // set tree navs
                Set<SecurityNav> navs = cache.getSecurityNavsByAuthorities(authorities);
                context.setNavs(TreeSecurityNav.build(navs));

                // set session attribute
                session.setAttribute(FORT_SESSION_NAME, context);

                response.addHeader("Set-Cookie", String.format("%s=%s; Path=/; HttpOnly", FORT_USER_TOKEN_COOKIE_NAME, user.getToken()));

                // signIn success, redirect to success return
                response.sendRedirect(configuration.getLogin().getSuccessReturn());
            } catch (FortAuthenticationException e) {
                // signIn or password error, redirect to error return
                response.sendRedirect(configuration.getLogin().getErrorReturn());
            } catch (Exception e) {
                log.error("signIn error", e);
            }
        }

        /**
         * logout handler. remove cookie JSESSIONID FORT_USER_TOKEN_COOKIE_NAME
         *
         * @param response http servlet response
         * @throws IOException
         */
        private void logout(HttpServletResponse response) throws IOException {
            response.addHeader("Set-Cookie", "JSESSIONID=; Path=/; HttpOnly");
            response.addHeader("Set-Cookie", String.format("%s=; Path=/; HttpOnly", FORT_USER_TOKEN_COOKIE_NAME));
            response.sendRedirect(configuration.getLogout().getSuccessReturn());
        }

        /**
         * set fort content. get session attribute , session name FortContextHolder.FORT_SESSION_NAME
         *
         * @param request http servlet request
         */
        private void setFortContext(HttpServletRequest request) {
            Object attr = request.getSession().getAttribute(FORT_SESSION_NAME);
            if (attr != null) {
                setContext((FortContext) attr);
            } else {
                setContext(FortContext.createEmptyContext());
            }
        }

        private void authentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Long resourceId) throws IOException, ServletException {
            // setFortContext(request);
            Object attr = request.getSession().getAttribute(FORT_SESSION_NAME);

            if (attr == null) {
                // no logged, redirect to signIn view
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
            for (SecurityAuthority authority : authorities) {
                for (SecurityAuthority userAuthority : userAuthorities) {
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
