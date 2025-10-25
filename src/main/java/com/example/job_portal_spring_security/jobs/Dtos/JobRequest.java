package com.example.job_portal_spring_security.jobs.Dtos;
import com.example.job_portal_spring_security.jobs.JobType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class JobRequest {
    @NotBlank private String title;
    @NotBlank private String description;
    @NotBlank private String category;
    @NotBlank private String location;
    @NotNull @DecimalMin("0.0") private BigDecimal salary;
    @NotNull private JobType type;

    // getters/setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    public JobType getType() { return type; }
    public void setType(JobType type) { this.type = type; }
}

