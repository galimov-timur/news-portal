package kz.epam.newsportal.util;

import io.jsonwebtoken.*;
import kz.epam.newsportal.model.User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

@Service
public class JwtUtil {

    private static final String SECRET_KEY = "secret";
    private static final int HALF_HOUR = 1000 * 60 * 30;
    private static final String AUTHORITIES_CLAIM = "authorities";

    public String extractUserEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(User userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(AUTHORITIES_CLAIM, userDetails.getAuthorities());
        return createToken(claims, userDetails.getEmail());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + HALF_HOUR))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public Boolean validateToken(String token, User userDetails) {
        final String userEmail = extractUserEmail(token);
        return (userEmail.equals(userDetails.getEmail()) && !isTokenExpired(token));
    }
}
