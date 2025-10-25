package com.example.job_portal_spring_security.jobs.repository;

import com.example.job_portal_spring_security.jobs.Job;
import com.example.job_portal_spring_security.jobs.JobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JobRepository extends JpaRepository<Job, Long> {

    // public search with optional filters
    @Query("""
    SELECT j FROM Job j
    WHERE (:q IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(j.description) LIKE LOWER(CONCAT('%', :q, '%')))
      AND (:category IS NULL OR j.category = :category)
      AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
      AND (:type IS NULL OR j.type = :type)
    """)
    Page<Job> search(String q, String category, String location, JobType type, Pageable pageable);

    Page<Job> findByPostedBy_Email(String email, Pageable pageable);
}


