package com.example.demo.services.impl;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.domain.CurrentUser;
import com.example.demo.domain.User;
import com.example.demo.repositories.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository, 
        PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public CurrentUser loadUserByUsername(String email) throws UsernameNotFoundException {
        return this.userRepository.findByEmail(email)
                    .map(CurrentUser::new)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public CurrentUser save(User user) {
        
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        user.setPassword(this.encoder.encode(user.getPassword()));
        User savedUser = this.userRepository.save(user);
        return this.loadUserByUsername(savedUser.getEmail());
    }

}
