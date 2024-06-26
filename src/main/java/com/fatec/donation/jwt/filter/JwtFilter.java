package com.fatec.donation.jwt.filter;

import com.fatec.donation.domain.entity.User;
import com.fatec.donation.jwt.InvalidTokenException;
import com.fatec.donation.jwt.JwtService;
import com.fatec.donation.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = getToken(request);
        if (token != null) {
            try {
                if (!jwtService.isTokenBlacklisted(token)) {
                    String email = jwtService.getEmailFromToken(token);
                    User user = userService.getByEmail(email);
                    setUserAsAuthenticated(user);
                } else {
                    log.warn("Token na lista negra: {}", token);
                }
            } catch (InvalidTokenException e) {
                log.error("Token Inválido: {}", e.getMessage());
            } catch (Exception e) {
                log.error("Erro na validação do token: {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }

    private void setUserAsAuthenticated(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password("N/A")
                .authorities(authorities)
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            String[] authHeaderParts = authHeader.split(" ");
            if (authHeaderParts.length == 2) {
                return authHeaderParts[1];
            }
        }
        return null;
    }
}
