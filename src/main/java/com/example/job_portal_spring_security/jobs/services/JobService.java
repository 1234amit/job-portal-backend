package com.example.job_portal_spring_security.jobs.services;

import com.example.job_portal_spring_security.jobs.Dtos.JobRequest;
import com.example.job_portal_spring_security.jobs.Job;
import com.example.job_portal_spring_security.jobs.JobType;
import com.example.job_portal_spring_security.jobs.repository.JobRepository;
import com.example.job_portal_spring_security.jobs.response.JobResponse;
import com.example.job_portal_spring_security.user.User;
import com.example.job_portal_spring_security.user.UserRepository;
import com.example.job_portal_spring_security.user.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobService {
    private final JobRepository jobRepo;
    private final UserRepository userRepository;

    public JobService(JobRepository jobRepo, UserRepository userRepository){
        this.jobRepo = jobRepo;
        this.userRepository = userRepository;
    }

    private static JobResponse toDto(Job j) {
        return new JobResponse(
                j.getId(), j.getTitle(), j.getDescription(), j.getCategory(), j.getLocation(),
                j.getSalary(), j.getType(), j.getPostedBy().getEmail(), j.getCreatedAt()
        );
    }

    private static String currentEmail() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // CREATE
//    @Transactional
//    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
//    public JobResponse create(JobRequest r) {
//        String email = currentEmail();
//        User employer = userRepository.findByEmail(email).orElseThrow();
//
//        Job j = new Job();
//        j.setTitle(r.getTitle());
//        j.setDescription(r.getDescription());
//        j.setCategory(r.getCategory());
//        j.setLocation(r.getLocation());
//        j.setSalary(r.getSalary());
//        j.setType(r.getType());
//        j.setPostedBy(employer);
//
//        return toDto(jobRepo.save(j));
//    }

    @Transactional
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    public JobResponse create(JobRequest r) {
        String email = currentEmail();
        User employer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job j = new Job();
        j.setTitle(r.getTitle());
        j.setDescription(r.getDescription());
        j.setCategory(r.getCategory());
        j.setLocation(r.getLocation());
        j.setSalary(r.getSalary());
        j.setType(r.getType());
        j.setPostedBy(employer);  // Make sure this is not null

        return toDto(jobRepo.save(j));
    }


    // UPDATE (owner or ADMIN)
    @Transactional
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    public JobResponse update(Long id, JobRequest r) {
        Job j = jobRepo.findById(id).orElseThrow();
        String email = currentEmail();
        boolean isOwner = j.getPostedBy().getEmail().equals(email);

        boolean isAdmin = userRepository.findByEmail(email)
                .orElseThrow()
                .getRoles().contains(UserRole.ADMIN);

        if (!isOwner && !isAdmin) throw new RuntimeException("Forbidden: not owner");

        j.setTitle(r.getTitle());
        j.setDescription(r.getDescription());
        j.setCategory(r.getCategory());
        j.setLocation(r.getLocation());
        j.setSalary(r.getSalary());
        j.setType(r.getType());

        return toDto(jobRepo.save(j));
    }

    // DELETE (owner or ADMIN)
    @Transactional
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    public void delete(Long id) {
        Job j = jobRepo.findById(id).orElseThrow();
        String email = currentEmail();
        boolean isOwner = j.getPostedBy().getEmail().equals(email);

        boolean isAdmin = userRepository.findByEmail(email)
                .orElseThrow()
                .getRoles().contains(UserRole.ADMIN);

        if (!isOwner && !isAdmin) throw new RuntimeException("Forbidden: not owner");
        jobRepo.delete(j);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('CANDIDATE','EMPLOYER','ADMIN')")
    public JobResponse get(Long id) {
        Job job = jobRepo.findById(id)
                .map(j -> {
                    if (j.getPostedBy() != null) {
                        j.getPostedBy().getEmail();  // Ensure postedBy is not null before accessing
                    }
                    return j;
                })
                .orElseThrow(() -> new RuntimeException("Job not found"));

        return toDto(job);
    }


    // SEARCH (any authenticated user)
    @PreAuthorize("hasAnyRole('CANDIDATE','EMPLOYER','ADMIN')")
    public Page<JobResponse> search(String q, String category, String location, JobType type,
                                    int page, int size, String sort) {
        Sort s = Sort.by(Sort.Order.desc("createdAt"));
        if (sort != null && !sort.isBlank()) s = Sort.by(sort.split(",").length == 2
                ? new Sort.Order(Sort.Direction.fromString(sort.split(",")[1]), sort.split(",")[0])
                : Sort.Order.asc(sort));
        Page<Job> rs = jobRepo.search(
                (q == null || q.isBlank()) ? null : q,
                (category == null || category.isBlank()) ? null : category,
                (location == null || location.isBlank()) ? null : location,
                type,
                PageRequest.of(page, size, s)
        );
        return rs.map(JobService::toDto);
    }

    // List jobs by current employer
    @PreAuthorize("hasAnyRole('EMPLOYER','ADMIN')")
    public Page<JobResponse> myJobs(int page, int size) {
        String email = currentEmail();
        return jobRepo.findByPostedBy_Email(email, PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(JobService::toDto);
    }


}
