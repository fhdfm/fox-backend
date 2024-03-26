package com.example.demo.config;

import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback;

import com.example.demo.domain.Course;
import com.example.demo.domain.User;

@Configuration
public class UUIDConfig {
    
    @Bean
    BeforeConvertCallback<User> beforeConvert_User() {
        return (user) -> {
            if (user.getId() == null) {
                user.setId(UUID.randomUUID().toString());
            }
            return user;
        };
    }

    @Bean
    BeforeConvertCallback<Course> beforeConvert_Course() {
        return (course) -> {
            if (course.getId() == null) {
                course.setId(UUID.randomUUID().toString());
            }
            return course;
        };
    }

}
