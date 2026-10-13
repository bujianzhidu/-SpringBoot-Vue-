package com.example.demo.security;

import com.example.demo.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            logger.info("Processing request: {} {}", request.getMethod(), request.getRequestURI());
            logger.info("JWT from request: {}", jwt);

            if (jwt != null) {
                logger.info("Validating token...");
                if (jwtUtil.validateToken(jwt)) {
                    logger.info("Token is valid");
                    Long userId = jwtUtil.getUserIdFromToken(jwt);
                    logger.info("User ID from token: {}", userId);
                    
                    try {
                        UserDetails userDetails = userDetailsService.loadUserById(userId);
                        logger.info("User details loaded successfully: {}", userDetails.getUsername());
                        logger.info("User authorities: {}", userDetails.getAuthorities());
                        
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.info("Authentication set in SecurityContext successfully");
                    } catch (Exception e) {
                        logger.error("Error loading user details: {}", e.getMessage(), e);
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        return;
                    }
                } else {
                    logger.warn("Invalid JWT token");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            } else {
                logger.info("No JWT token found in request");
            }
        } catch (Exception e) {
            logger.error("Error processing JWT: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        logger.info("Authorization header: {}", bearerToken);
        
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            logger.info("Extracted token: {}", token);
            return token;
        }
        return null;
    }
} 