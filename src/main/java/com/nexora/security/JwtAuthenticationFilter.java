package com.nexora.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        String authorizationHeader =
                request.getHeader("Authorization");

        // Debugging: Check whether the filter is running.
        System.out.println("JWT Filter Called: " + requestURI);

        boolean hasBearerToken =
                authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ");

        System.out.println(
                "Authorization Header Present: " + hasBearerToken
        );

        // If the token is missing, continue the filter chain.
        // Spring Security will decide whether the endpoint is allowed.
        if (!hasBearerToken) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7).trim();

        if (token.isEmpty()) {
            System.out.println("JWT Error: Empty token");

            SecurityContextHolder.clearContext();

            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract email from JWT.
            String email = jwtService.extractEmail(token);

            if (email == null || email.isBlank()) {
                System.out.println("JWT Error: Email is missing");

                SecurityContextHolder.clearContext();

                filterChain.doFilter(request, response);
                return;
            }

            // Authenticate only if no authentication already exists.
            if (SecurityContextHolder.getContext()
                    .getAuthentication() == null) {

                // Validate the token.
                if (jwtService.isTokenValid(token, email)) {

                    // Load user from MongoDB.
                    UserDetails userDetails =
                            userDetailsService.loadUserByUsername(email);

                    // Create authenticated token.
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    // Store authentication in Spring Security context.
                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);

                    System.out.println(
                            "JWT User Authenticated: "
                                    + authentication.getName()
                    );

                } else {
                    System.out.println("JWT Error: Token is invalid");
                    SecurityContextHolder.clearContext();
                }
            }

        } catch (JwtException | IllegalArgumentException exception) {

            // Do not print the actual JWT.
            System.err.println(
                    "JWT Validation Failed: "
                            + exception.getClass().getSimpleName()
                            + " - "
                            + exception.getMessage()
            );

            SecurityContextHolder.clearContext();

        } catch (UsernameNotFoundException exception) {

            System.err.println(
                    "JWT User Not Found: " + exception.getMessage()
            );

            SecurityContextHolder.clearContext();
        }

        // Continue to the controller/security authorization rules.
        filterChain.doFilter(request, response);
    }
}