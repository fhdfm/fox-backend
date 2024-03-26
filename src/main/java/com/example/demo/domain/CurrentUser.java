package com.example.demo.domain;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CurrentUser implements UserDetails {

    private final User user;

    public CurrentUser(User user) {
        this.user = user;
    }

    public String getName() {
        return this.user.getName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      if (this.user.getRole() == UserRole.ADMIN)
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), 
            new SimpleGrantedAuthority("ROLE_USER"));
      return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }


    @Override
    public String getUsername() {
       return this.user.getEmail();
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
