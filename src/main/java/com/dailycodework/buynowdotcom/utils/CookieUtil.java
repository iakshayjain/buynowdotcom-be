package com.dailycodework.buynowdotcom.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Slf4j
public class CookieUtil {

    @Value("${app.useSecureCookie}")
    private boolean useSecureCookie;

    public void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken, long maxAge) {
        if(response==null) {
            throw new IllegalArgumentException("HttpServletResponse cannot be null");
        }

        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        cookie.setSecure(useSecureCookie);
        cookie.setMaxAge((int) maxAge/1000);
        String sameSite = useSecureCookie ? "None" : "Lax";
        String secure = useSecureCookie ? "Secure; " : "";
        cookie.setAttribute("SameSite", sameSite);
        response.addCookie(cookie);
        setResponseHeader(response, cookie, sameSite, secure);
    }

    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if("refresh_token".equals(cookie.getName())) {
                    log.info("cookie found: {}", cookie.getName());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void printCookieLogs(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            log.info("{} cookies found", cookies.length);
            for (Cookie cookie : cookies) {
                log.info("cookie name: {}", cookie.getName());
            }
        }
    }

    private void setResponseHeader(HttpServletResponse response, Cookie cookie, String sameSite, String secure) {
        String cookieHeaderValue =
                cookie.getName() + "=" + cookie.getValue() + "; " +
                "HttpOnly" + "; " +
                "Path" + "=" + cookie.getPath() + "; " +
                "Max-Age" + "=" + cookie.getMaxAge() + "; " +
                secure +
                "SameSite" + "=" + sameSite;

        response.setHeader("Set-Cookie", cookieHeaderValue);
    }
}