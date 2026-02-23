package com.carmoneypit.engine.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TrailingSlashRedirectFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        // Redirect 301. Preserve root "/"
        if (requestUri != null && requestUri.endsWith("/") && requestUri.length() > 1) {
            String newUri = requestUri.substring(0, requestUri.length() - 1);
            String queryString = request.getQueryString();

            if (queryString != null && !queryString.isEmpty()) {
                newUri += "?" + queryString;
            }

            response.setStatus(HttpStatus.MOVED_PERMANENTLY.value());
            response.setHeader("Location", newUri);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
