package com.tracku.chris.tracku.Services;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${spring.app.secret}")
    private String ACCESS_TOKEN_SECRET;
    private static final long EXPIRY_TIME = 1000 * 60 * 20;

    /**
     * Creates and returns a JWT token using the user details
     * and extraClaims
     * @param extraClaims Possible extra claims like roles or any other custom claim.
     * @param username The user's email
     * @return jwt token
     * */
    public String createToken(Map<String, Object> extraClaims, @NonNull String username) {

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRY_TIME))
                .signWith(getJwtSecret(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Creates and returns a JWT token using the user details
     * @param username The user's email
     * @return JWT token
     * */
    public String createToken(@NonNull String username) {
        return createToken(new HashMap<>(), username);
    }

    /**
     * Verifies if the token is valid.
     * @param token The JWT token
     * @param currentUsername The user's email
     * */
    public Boolean isTokenValid(String token, String currentUsername) {
        final String username = extractUsername(token);
        final Date expirationDate = extractClaimFromToken(token, Claims::getExpiration);
        return username.equals(currentUsername) && expirationDate.after(new Date());
    }

    /**
     * Extracts the username(Subject) from the JWT token
     * @param token JWT token
     * @return JWT token
     * */
    public String extractUsername(String token) {
        return extractClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the token.
     * @param token JWT token
     * @param elementFinder Function from the Claims interface
     * @return Claim's value
     * */
    private <T> T extractClaimFromToken(String token, Function<Claims, T> elementFinder) {
        final Claims claims = extractClaims(token);
        return elementFinder.apply(claims);
    }

    /**
     * Extracts all claims from a token
     * @param token JWT Token
     * @return Claims
     * */
    private Claims extractClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getJwtSecret())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Decodes and returns the secret(signing key)
     * @return Key
     * */
    private Key getJwtSecret() {
        byte[] secretBytes = Decoders.BASE64.decode(ACCESS_TOKEN_SECRET);
        return Keys.hmacShaKeyFor(secretBytes);
    }
}
