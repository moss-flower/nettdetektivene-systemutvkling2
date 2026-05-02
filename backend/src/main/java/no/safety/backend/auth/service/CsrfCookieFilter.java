package no.safety.backend.auth.service;

import java.io.IOException;

import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filter that ensures CSRF token is written to a cookie on each request.
 * Extends OncePerRequestFilter to guarantee a single execution per request.
 */
public class CsrfCookieFilter extends OncePerRequestFilter {
    /**
     * Intercepts requests to ensure the CSRF token is written to a cookie.
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain to continue processing
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            csrfToken.getToken(); // forces the token to be written to the cookie
        }
        filterChain.doFilter(request, response);
    }
}
