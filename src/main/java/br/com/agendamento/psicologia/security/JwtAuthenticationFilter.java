package br.com.agendamento.psicologia.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = extrairToken(request);

        if (token != null
                && SecurityContextHolder.getContext()
                .getAuthentication() == null) {
            autenticar(token, request);
        }

        filterChain.doFilter(request, response);
    }

    private void autenticar(
            String token,
            HttpServletRequest request
    ) {
        try {
            Jwt jwt = jwtService.decodificarToken(token);

            UserDetails userDetails = userDetailsService
                    .loadUserByUsername(jwt.getSubject());

            if (!jwtService.pertenceAoUsuario(jwt, userDetails)) {
                return;
            }

            var authentication =
                    UsernamePasswordAuthenticationToken.authenticated(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        } catch (JwtException | AuthenticationException exception) {
            SecurityContextHolder.clearContext();
        }
    }

    private String extrairToken(HttpServletRequest request) {
        String authorization = request.getHeader(
                AUTHORIZATION_HEADER
        );

        if (authorization != null
                && authorization.startsWith(BEARER_PREFIX)) {
            String token = authorization.substring(
                    BEARER_PREFIX.length()
            );

            if (!token.isBlank()) {
                return token;
            }
        }

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> JwtService.COOKIE_NAME.equals(
                        cookie.getName()
                ))
                .map(Cookie::getValue)
                .filter(token -> !token.isBlank())
                .findFirst()
                .orElse(null);
    }
}