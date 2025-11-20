package com.example.cotato_networking.global.security;

import com.example.cotato_networking.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private Long id;
    private String loginId;
    private String password;
    private Collection<GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.loginId = user.getLoginId();
        this.password = user.getPassword();
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    public Long getUserId() {
        return id;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return loginId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
