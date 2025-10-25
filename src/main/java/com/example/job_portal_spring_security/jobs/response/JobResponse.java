package com.example.job_portal_spring_security.jobs.response;

import com.example.job_portal_spring_security.jobs.JobType;

import java.math.BigDecimal;
import java.time.Instant;

public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String location;
    private BigDecimal salary;
    private JobType type;
    private String postedByEmail;
    private Instant createdAt;

    public JobResponse() {}

    public JobResponse(Long id, String title, String description, String category, String location,
                       BigDecimal salary, JobType type, String postedByEmail, Instant createdAt) {
        this.id = id; this.title = title; this.description = description;
        this.category = category; this.location = location; this.salary = salary;
        this.type = type; this.postedByEmail = postedByEmail; this.createdAt = createdAt;
    }

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public String getPostedByEmail() { return postedByEmail; }
    public void setPostedByEmail(String postedByEmail) { this.postedByEmail = postedByEmail; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

