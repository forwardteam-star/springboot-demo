package com.darcytech.demo.web.security;

import java.util.List;

import javax.annotation.Nullable;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Operator implements Authentication {

    private Long operatorId;

    private String operatorName;

    private Long userId;

    private List<? extends GrantedAuthority> authorities;

    private String mainSessionKey;

    @Nullable
    private String subSessionKey;

    @Nullable
    private Boolean authenticated;

    @Override
    public Object getCredentials() {
        throw new UnsupportedOperationException("");
    }

    @Override
    public Object getDetails() {
        throw new UnsupportedOperationException("");
    }

    @Override
    public String getName() {
        return operatorName;
    }

    @Override
    public Object getPrincipal() {
        return this;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated != null && authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        authenticated = isAuthenticated;
    }

    public boolean mainUser() {
        return !operatorName.contains(":");
    }

}
