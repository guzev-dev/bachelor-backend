package bachelor.proj.charity.pl.authorization.jwt;

import bachelor.proj.charity.pl.exceptions.ExpiredTokenException;
import bachelor.proj.charity.shared.enums.UserRole;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JWTokenProvider {

    private final UserDetailsService userDetailsService;

    @Value("${auth.jwt.key}")
    private String key;

    @Value("${auth.jwt.header}")
    private String headerName;

    @Value("${auth.jwt.expiration-time}")
    private Long expirationTime;

    @Value("${auth.jwt.prefix}")
    private String prefix;

    @PostConstruct
    private void init() {

        //encode key and set
        final String newKey = Base64.getEncoder()
                .encodeToString(key.getBytes());

        if (newKey != null)
            key = newKey;
    }

    public String createToken(String email, UserRole role, Long userId) {

        //set JWT payload claims
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role",role);
        claims.put("id",userId);
        //set JWT validity time
        final Date now = new Date(),
            expireIn = new Date(now.getTime() + expirationTime * 1000);

        //build and return token
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expireIn)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public boolean isTokenValid(String token) {

        //check if JWT is not expired
        Date expireIn = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();

        if (expireIn.after(new Date()))
            return true;
        else
            throw new ExpiredTokenException("JSON Web Token expired.");
    }

    public Authentication getAuthentication(String token) {

        //extract user email from token
        final String userEmail = getUserEmail(token);
        //finding user details by user email
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        //return new authentication
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserEmail(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Long getUserId(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);
    }

    public Long getUserId(HttpServletRequest request) {

        String token = request.getHeader(headerName);
        if (token != null && token.startsWith(prefix)) {
            token = token.substring(prefix.length());

            return Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody()
                    .get("id", Long.class);
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Відсутній заголовок для авторизації користувача.");
    }

    public String extractToken(HttpServletRequest request) {
        //check if token is present and start with correct prefix
        String token = request.getHeader(headerName);
        if (token != null && token.startsWith(prefix)) {
            return token.substring(prefix.length());
        }

        return null;
    }

}
