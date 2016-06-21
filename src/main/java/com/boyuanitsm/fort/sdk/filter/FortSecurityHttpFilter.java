package com.boyuanitsm.fort.sdk.filter;

import com.boyuanitsm.fort.sdk.cache.FortResourceCache;
import com.boyuanitsm.fort.sdk.client.FortClient;
import com.boyuanitsm.fort.sdk.config.FortConfiguration;
import com.boyuanitsm.fort.sdk.context.FortContext;
import com.boyuanitsm.fort.sdk.context.FortContextHolder;
import com.boyuanitsm.fort.sdk.domain.*;
import com.boyuanitsm.fort.sdk.exception.FortAuthenticationException;
import com.boyuanitsm.fort.sdk.exception.FortCrudException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

import static com.boyuanitsm.fort.sdk.config.Constants.*;

/**
 * Fort Security Http Filter. Intercept request to achieve the purpose of access control.
 * Login logout operations are handled by the filter.Configure the filter must be used in the spring agent.
 *
 * @author zhanghua on 5/17/16.
 */
@Component
public class FortSecurityHttpFilter implements Filter {

    private final Logger log = LoggerFactory.getLogger(FortSecurityHttpFilter.class);

    @Autowired
    private FortConfiguration configuration;

    @Autowired
    private FortClient client;

    @Autowired
    private FortResourceCache cache;

    private String contextPath = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        // on init
    }

    public void destroy() {
        // on destroy
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (contextPath == null) {
            contextPath = request.getContextPath();
        }

        String requestUri = request.getRequestURI();

        if (!contextPath.isEmpty()) {
            // fix context path
            requestUri = requestUri.replace(contextPath, "");
        }

        log.debug("request uri: {}", requestUri);

        if (configuration.getLogin().getUrl().equals(requestUri)) {
            signIn(request, response);
        } else if (configuration.getLogout().getUrl().equals(requestUri)) {
            logout(request, response);
        } else if (cache.getResourceId(requestUri) != null) {
            Long resourceId = cache.getResourceId(requestUri);
            authentication(request, response, chain, resourceId);
        } else {
            fortContext(request);
            chain.doFilter(request, response);
        }
    }

    /**
     * When the resources were not add access control set fort context.
     *
     * @param request the HTTP request
     */
    private void fortContext(HttpServletRequest request) {
        String token = getCookieValue(request.getCookies(), FORT_USER_TOKEN_COOKIE_NAME);
        FortContext context = cache.getFortContext(token);

        if (context != null) {
            FortContextHolder.setContext(context);
        }
    }

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
            SecurityUser user = client.signIn(login, password, request.getRemoteAddr(), request.getHeader(USER_AGENT));
            // update logged user cache.
            cache.updateLoggedUserCache(user);
            // set cookie
            response.addHeader("Set-Cookie", String.format("%s=%s; Path=/; HttpOnly", FORT_USER_TOKEN_COOKIE_NAME, user.getToken()));
            // signIn success, redirect to success return
            sendRedirect(response, configuration.getLogin().getSuccessReturn());
        } catch (FortAuthenticationException e) {
            // signIn or password error, redirect to error return
            sendRedirect(response, configuration.getLogin().getErrorReturn());
        } catch (Exception e) {
            log.error("signIn error", e);
        }
    }

    /**
     * logout handler. remove cookie FORT_USER_TOKEN_COOKIE_NAME
     *
     * @param request  http servlet request
     * @param response http servlet response
     * @throws IOException
     */
    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // clear cookie
        response.addHeader("Set-Cookie", String.format("%s=; Path=/; HttpOnly", FORT_USER_TOKEN_COOKIE_NAME));
        // token overdue
        String token = getCookieValue(request.getCookies(), FORT_USER_TOKEN_COOKIE_NAME);
        try {
            client.logout(token);
        } catch (FortCrudException e) {
            log.warn("token overdue error! ", e);
        }
        sendRedirect(response, configuration.getLogout().getSuccessReturn());
    }

    /**
     * Get cookie value from cookies
     *
     * @param cookies    the cookie array
     * @param cookieName the name of the cookie
     * @return cookie value
     */
    private String getCookieValue(Cookie[] cookies, String cookieName) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * authentication access. if token is null. redirect to login view.
     *
     * @param request    the servlet request
     * @param response   the servlet response
     * @param chain      the filter chain
     * @param resourceId the resource id
     * @throws IOException
     * @throws ServletException
     */
    private void authentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Long resourceId) throws IOException, ServletException {
        String token = getCookieValue(request.getCookies(), FORT_USER_TOKEN_COOKIE_NAME);
        FortContext context = cache.getFortContext(token);

        if (context == null) {
            // no logged, redirect to signIn view
            sendRedirect(response, configuration.getLogin().getLoginView());
            return;
        }

        // get this resource relation authorities
        Set<Long> authorityIdSet = cache.getAuthorityIdSet(resourceId);
        // get user authorities
        Set<SecurityAuthority> userAuthorities = context.getAuthorities();

        boolean isAllow = false;

        if (authorityIdSet != null) {
            for (Long authorityId : authorityIdSet) {
                for (SecurityAuthority userAuthority : userAuthorities) {
                    if (authorityId.equals(userAuthority.getId())) {
                        isAllow = true;
                        break;
                    }
                }
                if (isAllow) {
                    break;
                }
            }
        }

        if (isAllow) {// ok
            // set context
            FortContextHolder.setContext(context);
            // do filter
            chain.doFilter(request, response);
        } else {// un authorized
            sendRedirect(response, configuration.getAuthentication().getUnauthorizedReturn());
        }
    }

    /**
     * Send redirect and add contextPath
     *
     * @param response the HTTP response
     * @param s        redirect url
     */
    private void sendRedirect(HttpServletResponse response, String s) throws IOException {
        response.sendRedirect(contextPath + s);
    }
}
