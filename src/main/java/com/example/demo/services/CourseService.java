package com.example.demo.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Course;
import com.example.demo.dto.CourseDTO;
import com.example.demo.repositories.CourseRepository;

@Service
public class CourseService {
    
    private final StorageService storageService;

    private final CourseRepository courseRepository;

    public CourseService(StorageService storageService, 
        CourseRepository courseRepository) {
        this.storageService = storageService;
        this.courseRepository = courseRepository;
    }

    public void save(CourseDTO courseDTO) {
        
        byte[] imageBytes = courseDTO.getImageBytes();
        if (imageBytes != null) {
            String imageUrl = storageService.upload(imageBytes);
            courseDTO.setImage(imageUrl);
        }

        validateFields(courseDTO);

        this.courseRepository.save(new Course(courseDTO));
    }

    private void validateFields(CourseDTO courseDTO) {
        if (courseDTO.getName() == null) {
            throw new IllegalArgumentException("Título é requerido.");
        }

        if (courseDTO.getDescription() == null) {
            throw new IllegalArgumentException("Descrição é requerida.");
        }

        if (courseDTO.getPrice() == null) {
            throw new IllegalArgumentException("Preço é requerido.");
        }
    }

    public void delete(String id) {
        
        if (id == null) {
            throw new IllegalArgumentException("Id é requerido.");
        }

        if (!this.courseRepository.existsById(id)) {
            throw new IllegalArgumentException("Curso não encontrado.");
        }

        this.courseRepository.deleteById(id);
    }

    public CourseDTO findById(String id) {
        
        if (id == null) {
            throw new IllegalArgumentException("Id é requerido.");
        }

        Course course = 
        this.courseRepository.findById(id).orElse(null);

        if (course == null) {
            throw new IllegalArgumentException("Curso não encontrado.");
        }

        return new CourseDTO(course);
    }

    public Page<CourseDTO> findAll(Pageable pageable) {
        return CourseDTO.toDTOList(this.courseRepository.findAll(pageable));
    }

}
