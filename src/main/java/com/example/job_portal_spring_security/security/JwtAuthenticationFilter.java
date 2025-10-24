package com.example.job_portal_spring_security.security;
import com.example.job_portal_spring_security.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.stream.Collectors;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final TokenBlacklist tokenBlacklist;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository, TokenBlacklist tokenBlacklist) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.tokenBlacklist = tokenBlacklist;
    }
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String header = request.getHeader("Authorization");
//        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
//            String token = header.substring(7);
//            try {
//                String email = jwtUtil.getSubject(token);
//                var user = userRepository.findByEmail(email).orElse(null);
//                if (user != null) {
//                    var authorities = user.getRoles().stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r.name())).collect(Collectors.toSet());
//                    var auth = new UsernamePasswordAuthenticationToken(email, null, authorities);
//                    SecurityContextHolder.getContext().setAuthentication(auth);
//                }
//            } catch (Exception ignored) {}
//        }
//        filterChain.doFilter(request, response);
//
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            // NEW: block blacklisted tokens
            if (tokenBlacklist.isBlacklisted(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            try {
                String email = jwtUtil.getSubject(token);
                var user = userRepository.findByEmail(email).orElse(null);
                if (user != null) {
                    var authorities = user.getRoles().stream()
                            .map(r -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + r.name()))
                            .collect(java.util.stream.Collectors.toSet());
                    var auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(email, null, authorities);
                    org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception ignored) {}
        }
        filterChain.doFilter(request, response);
    }
}
