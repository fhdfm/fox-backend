package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.demo.domain.Course;

public interface CourseRepository extends PagingAndSortingRepository<Course, String> {
   
    @SuppressWarnings("all")
    Page<Course> findAll(Pageable pageable);

    Optional<Course> findById(String id);
    
    void deleteById(String id);
    
    boolean existsById(String id);
    
    Course save(Course course);
}
