package kz.epam.newsportal.filter;

import kz.epam.newsportal.model.User;
import kz.epam.newsportal.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHENTICATION_BEARER = "Bearer ";

    private UserDetailsService authenticationService;
    private JwtUtil jwtUtil;

    @Autowired
    public void setAuthenticationService(UserDetailsService authenticationService) {
        this.authenticationService = authenticationService;
    }
    @Autowired
    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION_HEADER);
        String userEmail = null;
        String token = null;

        if(authorizationHeader != null && authorizationHeader.startsWith(AUTHENTICATION_BEARER)) {
            token = authorizationHeader.substring(7);
            userEmail = jwtUtil.extractUserEmail(token);
        }

        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User userDetails = (User) authenticationService.loadUserByUsername(userEmail);
            if(jwtUtil.validateToken(token, userDetails)){
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
