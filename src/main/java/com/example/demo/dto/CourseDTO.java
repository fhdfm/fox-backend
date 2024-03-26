package com.example.demo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.data.domain.Page;

import com.example.demo.domain.Course;

public class CourseDTO implements Serializable {
    
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private Double discount;
    private String image;
    private byte[] imageBytes;
    private String level;
    private String state;
    private String city;
    private String committee;
    private String instructor;

    public CourseDTO() {
    }

    public CourseDTO(Course entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.price = entity.getPrice();
        this.discount = entity.getDiscount();
        this.image = entity.getImage();
        this.level = entity.getLevel();
        this.state = entity.getState();
        this.city = entity.getCity();
        this.committee = entity.getCommittee();
        this.instructor = entity.getInstructor();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCommittee() {
        return committee;
    }

    public void setCommittee(String committee) {
        this.committee = committee;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public static Page<CourseDTO> toDTOList(Page<Course> courses) {

        Page<CourseDTO> pageDTO = courses.map(new Function<Course, CourseDTO>() {
            public CourseDTO apply(Course course) {
                return new CourseDTO(course);
            }
        });

        return pageDTO;

    }
}


