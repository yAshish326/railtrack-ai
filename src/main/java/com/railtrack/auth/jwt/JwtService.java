// ============================================================================
// JwtService
// ----------------------------------------------------------------------------
// Responsibility:
// 1. Generate JWT after successful login
// 2. Extract information (claims) from JWT
// 3. Validate JWT
// 4. Check whether the token has expired
// ============================================================================

package com.railtrack.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    // Secret key used to sign and verify JWT.
    // Loaded from application.properties
    @Value("${jwt.secret}")
    private String secretKey;

    // JWT validity time in milliseconds.
    // Example: 86400000 = 24 Hours
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // ------------------------------------------------------------------------
    // Creates the signing key from the secret string.
    //
    // Why?
    // JWT must be digitally signed while creating it.
    // The same key is also used later to verify that
    // the token has not been modified.
    // ------------------------------------------------------------------------
    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8)
        );
    }

    // ------------------------------------------------------------------------
    // Generates a JWT after successful login.
    //
    // Information stored inside JWT:
    // • Username (Subject)
    // • Issued Time
    // • Expiration Time
    // • Digital Signature
    // ------------------------------------------------------------------------
    public String generateToken(UserDetails userDetails) {

        return Jwts.builder()

                // Store logged-in user's email/username
                .subject(userDetails.getUsername())

                // Token creation time
                .issuedAt(new Date(System.currentTimeMillis()))

                // Token expiry time
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))

                // Sign the token using our secret key
                .signWith(getSigningKey())

                // Convert everything into JWT String
                .compact();
    }

    // ------------------------------------------------------------------------
    // Reads the JWT and returns all information stored inside it.
    //
    // Think of this as opening an ID card and reading all its details.
    //
    // Claims contain:
    // • Username
    // • Expiration
    // • Issued Time
    // • Custom fields (if added)
    // ------------------------------------------------------------------------
    private Claims extractAllClaims(String token) {

        return Jwts.parser()

                // Verify JWT signature using secret key
                .verifyWith(getSigningKey())

                .build()

                // Parse the JWT
                .parseSignedClaims(token)

                // Return payload (claims)
                .getPayload();
    }

    // ------------------------------------------------------------------------
    // Returns the username stored inside the JWT.
    // ------------------------------------------------------------------------
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // ------------------------------------------------------------------------
    // Returns the expiry date stored inside the JWT.
    // ------------------------------------------------------------------------
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    // ------------------------------------------------------------------------
    // Checks whether the token has expired.
    //
    // Returns:
    // true  -> Token expired
    // false -> Token still valid
    // ------------------------------------------------------------------------
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ------------------------------------------------------------------------
    // Final JWT validation.
    //
    // A token is considered valid only if:
    // 1. Username inside JWT matches the logged-in user.
    // 2. Token has not expired.
    // ------------------------------------------------------------------------
    public boolean isTokenValid(String token, UserDetails userDetails) {

        String username = extractUsername(token);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }
}