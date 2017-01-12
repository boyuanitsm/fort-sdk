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

package com.boyuanitsm.fort.sdk;

import com.boyuanitsm.fort.sdk.client.ManagerClient;
import com.boyuanitsm.fort.sdk.config.FortProperties;
import com.boyuanitsm.fort.sdk.context.FortContext;
import com.boyuanitsm.fort.sdk.context.FortContextHolder;
import com.boyuanitsm.fort.sdk.domain.*;
import com.boyuanitsm.fort.sdk.exception.FortAuthenticationException;
import com.boyuanitsm.fort.sdk.exception.FortCrudException;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;

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
public class SecurityHttpFilter implements Filter {

    private final Logger log = LoggerFactory.getLogger(SecurityHttpFilter.class);

    @Autowired
    private FortProperties fortProperties;

    @Autowired
    private ManagerClient managerClient;

    @Autowired
    private ResourceManager cache;

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

        if (isIgnore(requestUri)) {
            // ignore this resource, do filter
            chain.doFilter(request, response);
            return;
        }

        // log.debug("request uri: {}", requestUri);

        Long resourceId = cache.getResourceId(requestUri);
        if (fortProperties.getLogin().getUrl().equals(requestUri)) {
            log.debug("Start fort login...");
            signIn(request, response);
        } else if (fortProperties.getLogout().getUrl().equals(requestUri)) {
            log.debug("Start fort logout...");
            logout(request, response);
        } else if (resourceId != null) {
            log.debug("Start fort authentication. resourceId: {}", resourceId);
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
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            log.warn("Login method not allowed, please use POST method!");
            response.setStatus(HttpStatus.SC_METHOD_NOT_ALLOWED);
            return;
        }

        String login = request.getParameter(LOGIN_FORM_USERNAME_PARAM_NAME);
        String password = request.getParameter(LOGIN_FORM_PASSWORD_PARAM_NAME);
        try {
            SecurityUser user = managerClient.signIn(login, password, request.getRemoteAddr(), request.getHeader(USER_AGENT));
            // update logged user cache.
            cache.updateLoggedUserCache(user);
            // set cookie
            Cookie cookie = new Cookie(FORT_USER_TOKEN_COOKIE_NAME, user.getToken());
            // set cookie domain
            String domain = fortProperties.getCookie().getDomain();
            if  (domain != null) {
                cookie.setDomain(domain);
            }
            cookie.setMaxAge(fortProperties.getCookie().getMaxAge());
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);

            // signIn success, redirect to success return
            String successReturn = request.getParameter(SUCCESS_RETURN);
            if (successReturn != null) {
                // have success_return param, return param value uri
                sendRedirect(response, successReturn);
            } else {
                // not have success_return param, return config uri
                sendRedirect(response, fortProperties.getLogin().getSuccessReturn());
            }
            log.debug("Fort login success! login: {}", login);
        } catch (FortAuthenticationException e) {
            log.warn("Authentication fail, login or password wrong, redirect to error return; login: {}, password: {} ErrorMsg:{}", login, password, e.getMessage());
            // login or password error, redirect to error return
            String errorReturn = request.getParameter(ERROR_RETURN);
            if (errorReturn != null) {
                // have error_return param, return param value uri
                sendRedirect(response, errorReturn);
            } else {
                // not have error_return param, return config uri
                sendRedirect(response, fortProperties.getLogin().getErrorReturn());
            }
        } catch (Exception e) {
            log.error("signIn error", e);
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
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
        Cookie cookie = new Cookie(FORT_USER_TOKEN_COOKIE_NAME, "");
        String domain = fortProperties.getCookie().getDomain();
        if (domain != null) {
            cookie.setDomain(fortProperties.getCookie().getDomain());
        }
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        // set token overdue
        String token = getCookieValue(request.getCookies(), FORT_USER_TOKEN_COOKIE_NAME);

        try {
            managerClient.logout(token);
        } catch (FortCrudException e) {
            log.warn("token overdue error! ", e);
        }

        log.debug("Fort logout success! token: {}", token);

        // logout success, redirect to success return
        String successReturn = request.getParameter(SUCCESS_RETURN);
        if (successReturn != null) {
            // have success_return param, return param value uri
            sendRedirect(response, successReturn);
        } else {
            // not have success_return param, return config uri
            sendRedirect(response, fortProperties.getLogout().getSuccessReturn());
        }
    }

    /**
     * Get cookie value from cookies
     *
     * @param cookies    the cookie array
     * @param cookieName the name of the cookie
     * @return cookie value
     */
    private String getCookieValue(Cookie[] cookies, String cookieName) {
        if (cookies == null) {
            return null;
        }
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
            log.debug("Not logged in, redirect to login view.");
            FortContextHolder.setContext(null);
            // no logged, redirect to signIn view
            sendRedirect(response, fortProperties.getLogin().getLoginView());
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
        } else {
            log.warn("This resource not have authority set! resourceId: {}", resourceId);
        }

        if (isAllow) {// ok
            log.debug("Authentication OK.");
            // set context
            FortContextHolder.setContext(context);
            // do filter
            chain.doFilter(request, response);
        } else {// un authorized
            log.warn("Access denied, redirect to unauthorized view.");
            sendRedirect(response, fortProperties.getAuthentication().getUnauthorizedReturn());
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

    private boolean isIgnore(String uri) {
        return PatternMatchUtils.simpleMatch(fortProperties.getIgnores(), uri);
    }
}
