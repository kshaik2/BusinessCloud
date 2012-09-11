package com.infor.cloudsuite.platform.components;

import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.infor.cloudsuite.service.StringDefs;

/**
 * User: bcrow
 * Date: 10/20/11 3:17 PM
 */
@Component
public class RequestServices {

    /**
     * Gets a UriBuilder based on the server context.
     *
     * @param request Request to build the uri from.
     * @return base UriBuilder
     */
    public UriBuilder getContextUriBuilder(HttpServletRequest request) {
        final StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(request.getScheme()).append("://").append(request.getServerName());
        if (request.getServerPort() > 0) {
            uriBuilder.append(":").append(request.getServerPort());
        }
        uriBuilder.append("/");
        return UriBuilder.fromUri(uriBuilder.toString()).path(request.getContextPath());
    }

    public void addObjectToSession(HttpServletRequest request, String name, Object object) {
        request.getSession(true).setAttribute(name, object);
    }

    public <T> T getObjectFromSession(HttpServletRequest request, String name) {
        //noinspection unchecked
        return (T) request.getSession().getAttribute(name);
    }

    public void removeObjectFromSession(HttpServletRequest request, String name) {
        request.getSession().removeAttribute(name);
    }

    private static final Logger logger = LoggerFactory.getLogger(RequestServices.class);

    public Locale getLocale(HttpServletRequest request) {
        return getLocale(request, null);
    }

    public Locale getLocale(HttpServletRequest request, String override) {
        Locale locale;

        if (override != null) {
            logger.debug("Override: " + override);
            return StringUtils.parseLocaleString(override);
        }
        final String cookieValue = getCookieValue(request);
        if (cookieValue != null) {
            locale = StringUtils.parseLocaleString(cookieValue);
        } else {
            locale = getLocale(request.getSession());
        }
        if (locale == null) {
            locale = Locale.US;
        }
        return locale;
    }

    private String getCookieValue(HttpServletRequest request) {
        String cookieValue = null;
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (StringDefs.SESSION_LOCALE.equals(cookie.getName())) {
                    cookieValue = cookie.getValue();
                    break;
                }
            }
            logger.debug("Cookie Value: " + cookieValue);
        }
        return cookieValue;
    }

    public Locale getLocale(HttpSession session) {
        final Object attribute = session.getAttribute(StringDefs.SESSION_LOCALE);
        return (Locale) attribute;
    }

    public void setLocale(HttpSession session, String parameter) {
        if (parameter != null) {
            setLocale(session, StringUtils.parseLocaleString(parameter));
        }
    }

    public void setLocale(HttpSession session, Locale locale) {
        if (locale != null) {
            session.setAttribute(StringDefs.SESSION_LOCALE, locale);
        }
    }

    public void setLocaleCookie(HttpServletResponse response, String localeString) {
        logger.debug("Setting locale cookie: " + localeString );
        Cookie cookie = new Cookie(StringDefs.SESSION_LOCALE, localeString);
        cookie.setMaxAge(999999999);
        response.addCookie(cookie);

    }
}
