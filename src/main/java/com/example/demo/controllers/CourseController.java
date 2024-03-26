package com.example.demo.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CourseDTO;
import com.example.demo.services.CourseService;

@RestController
public class CourseController {
    
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/api/courses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> save(@RequestBody CourseDTO courseDTO) {
        
        try {
            this.courseService.save(courseDTO);
            return new ResponseEntity<String>(
                "Curso criado com sucesso.", HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(
                e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @DeleteMapping("/api/courses/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable String id) {
        
        try {
            this.courseService.delete(id);
            return ResponseEntity.ok("Curso deletado com sucesso.");
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<String>(
                e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/courses")
    public ResponseEntity<Page<CourseDTO>> findAll(Pageable pageable) {
        
        try {
            return new ResponseEntity<Page<CourseDTO>>(
                this.courseService.findAll(pageable), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Page<CourseDTO>>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<CourseDTO> findById(@PathVariable String id) {
        
        try {
            return new ResponseEntity<CourseDTO>(
                this.courseService.findById(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<CourseDTO>(HttpStatus.BAD_REQUEST);
        }
    }
}
