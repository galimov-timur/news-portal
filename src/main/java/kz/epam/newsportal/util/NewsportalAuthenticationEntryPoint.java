package kz.epam.newsportal.util;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class NewsportalAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    private static final String STATUS_UNAUTHORISED = "HTTP Status 401 - ";

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = response.getWriter();
        writer.println(STATUS_UNAUTHORISED + authEx.getMessage());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setRealmName("NewsPortal");
        super.afterPropertiesSet();
    }
}
