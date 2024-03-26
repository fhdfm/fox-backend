package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.domain.User;

public interface UserRepository extends CrudRepository<User, String> {
 
    Optional<User> findByEmail(String email);

}
