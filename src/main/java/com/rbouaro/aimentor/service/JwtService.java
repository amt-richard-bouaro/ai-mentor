package com.rbouaro.aimentor.service;

import com.rbouaro.aimentor.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Instant now = Instant.now();
        
        String roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .collect(Collectors.joining(","));
        
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("ai-mentor")
                .issuedAt(now)
                .expiresAt(now.plus(jwtExpiration, ChronoUnit.MINUTES))
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .claim("roles", roles)
                .build();
        
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}