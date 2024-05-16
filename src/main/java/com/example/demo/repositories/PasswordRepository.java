package com.example.demo.repositories;

import com.example.demo.domain.Password;
import com.example.demo.repositories.custom.CustomCrudRepository;

public interface PasswordRepository extends CustomCrudRepository<Password, String> {
    
}
