package com.example.job_portal_spring_security.auth.AuthService;

import com.example.job_portal_spring_security.auth.dto.*;
import com.example.job_portal_spring_security.security.JwtUtil;
import com.example.job_portal_spring_security.security.TokenBlacklist;
import com.example.job_portal_spring_security.user.User;
import com.example.job_portal_spring_security.user.UserRepository;
import com.example.job_portal_spring_security.user.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;


    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, TokenBlacklist tokenBlacklist) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklist = tokenBlacklist;

    }

    public RegisterResponse register(RegisterRequest r) {
        if (userRepository.existsByEmail(r.getEmail()))
            throw new RuntimeException("Email already exists");

        Set<UserRole> roles = (r.getRoles() == null || r.getRoles().isEmpty())
                ? Set.of(UserRole.CANDIDATE)
                : r.getRoles().stream().map(s -> UserRole.valueOf(s.toUpperCase())).collect(Collectors.toSet());

        User user = new User();
        user.setEmail(r.getEmail());
        user.setPassword(passwordEncoder.encode(r.getPassword()));
        user.setFullName(r.getFullName());
        user.setRoles(roles);
        userRepository.save(user);

        String access = jwtUtil.generateAccessToken(user.getEmail());
        String refresh = jwtUtil.generateRefreshToken(user.getEmail());
        return new RegisterResponse("Register successfully", access, refresh);

    }

    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            throw new BadCredentialsException("Invalid credentials");

        String access = jwtUtil.generateAccessToken(user.getEmail());

        LoginResponse.UserInfo info = new LoginResponse.UserInfo(
                user.getEmail(),
                user.getFullName(),
                user.getRoles()
        );

        return new LoginResponse("Login successful", access, info);
    }


    public MessageResponse logout(String tokenOrHeader) {
        String token = tokenOrHeader;
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token is required");
        }
        if (token.startsWith("Bearer ")) token = token.substring(7);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtUtil.getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        long expMillis = claims.getExpiration().getTime();
        tokenBlacklist.blacklist(token, expMillis);
        return new MessageResponse("Logged out successfully");
    }




    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var authorities = user.getRoles().stream()
                .map(r -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + r.name()))
                .toList();
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }
}
