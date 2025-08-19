package com.rbouaro.aimentor.config.security;

import com.rbouaro.aimentor.entity.User;
import lombok.EqualsAndHashCode;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
public class UserAuthentication extends AbstractAuthenticationToken {

    private final Jwt jwt;

    private final transient User principal;

    public UserAuthentication(Jwt jwt, User principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.jwt = jwt;
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }



}