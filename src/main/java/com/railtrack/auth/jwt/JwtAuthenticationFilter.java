package com.railtrack.auth.jwt;

import com.railtrack.auth.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
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
 * 6. (Next Step) Store authenticated user in Spring Security Context.
 * ============================================================================
 */

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

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

        // Continue request processing
        filterChain.doFilter(request, response);
    }
}