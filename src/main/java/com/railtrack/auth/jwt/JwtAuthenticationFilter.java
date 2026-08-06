package com.railtrack.auth.jwt;

import com.railtrack.auth.security.CustomUserDetailsService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.io.IOException;

/*
 * ============================================================================
 * JwtAuthenticationFilter
 * ----------------------------------------------------------------------------
 * Responsibility:
 * 1. Runs once for every incoming HTTP request.
 * 2. Reads JWT from the Authorization header.
 * 3. Extracts username from JWT.
 * 4. Loads the latest user details from the database.
 * 5. Validates the JWT.
 * 6. Stores the authenticated user in the Spring Security Context.
 *
 * An expired, malformed, or tampered token is not a server error - it just
 * means "this request isn't authenticated". We catch JwtException here and
 * fall through with no authentication set, so downstream Spring Security
 * rejects the request with a clean 401/403 instead of this filter throwing
 * and producing a raw 500 stack trace in the logs.
 * ============================================================================
 */

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // Service used for extracting and validating JWT
    private final JwtService jwtService;

    // Service used for loading user details from the database
    private final CustomUserDetailsService userDetailsService;

    // Constructor Injection
    public JwtAuthenticationFilter(JwtService jwtService,
                                   CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Read Authorization header from incoming request
        String authHeader = request.getHeader("Authorization");

        // If header is missing or doesn't start with "Bearer ",
        // skip JWT validation and continue with the request.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Remove "Bearer " prefix and extract only the JWT
        String jwt = authHeader.substring(7);

        try {
            // Extract username/email from JWT
            String username = jwtService.extractUsername(jwt);

            // Load latest user details from the database
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Validate JWT
            if (jwtService.isTokenValid(jwt, userDetails)) {

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Add request details
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Tell Spring Security that this user is authenticated
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException | IllegalArgumentException | UsernameNotFoundException ex) {
            // Expired / malformed / tampered / unparseable token: treat as
            // "not authenticated" rather than letting this bubble up as a
            // 500. Clear any partial context just in case.
            log.debug("Ignoring invalid JWT on {} {}: {}",
                    request.getMethod(), request.getRequestURI(), ex.getMessage());
            SecurityContextHolder.clearContext();
        }

        // Continue request processing
        filterChain.doFilter(request, response);
    }
}