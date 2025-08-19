package com.rbouaro.aimentor.config.jwt;

import com.rbouaro.aimentor.constants.enums.UserPermission;
import com.rbouaro.aimentor.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JwtTokenGenerator {
    private final JwtEncoder jwtEncoder;
    private final JwtConfigProperties jwtConfigProperties;

    public String generateToken(User user) {
        Set<UserPermission> permissions = user.getPermissions();
        JwtClaimsSet claim = getClaims(user, permissions, jwtConfigProperties.jwtExpiration());
        return this.jwtEncoder.encode(JwtEncoderParameters.from(claim)).getTokenValue();
    }

    private JwtClaimsSet getClaims(User user, Set<UserPermission> permissions, long expirationMinutes) {
        Instant now = Instant.now();
        return JwtClaimsSet.builder().issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(expirationMinutes, ChronoUnit.DAYS))
                .subject(user.getEmail())
                .claim("perm", permissions)
                .build();
    }
}