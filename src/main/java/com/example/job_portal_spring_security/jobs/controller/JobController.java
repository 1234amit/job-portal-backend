package com.example.job_portal_spring_security.jobs.controller;

import com.example.job_portal_spring_security.jobs.Dtos.JobRequest;
import com.example.job_portal_spring_security.jobs.JobType;
import com.example.job_portal_spring_security.jobs.response.JobResponse;
import com.example.job_portal_spring_security.jobs.services.JobService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final JobService service;
    public JobController(JobService service){
        this.service = service;
    }

    //employees create
    @PostMapping("/create")
    public ResponseEntity<JobResponse> create(@Valid @RequestBody JobRequest r){
        return ResponseEntity.status(201).body(service.create(r));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> get(@PathVariable Long id){
        return ResponseEntity.ok(service.get(id));
    }

    /**
     * Update a job (EMPLOYER owner or ADMIN).
     */
    @PutMapping("/{id}")
    public ResponseEntity<JobResponse> update(@PathVariable Long id,
                                              @Valid @RequestBody JobRequest body) {
        return ResponseEntity.ok(service.update(id, body));
    }

    /**
     * Delete a job (EMPLOYER owner or ADMIN).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Search jobs (any authenticated role).
     * Query params (all optional):
     *  - q: free-text query
     *  - category
     *  - location
     *  - type: FULL_TIME | PART_TIME | CONTRACT | INTERNSHIP | TEMPORARY (match your enum)
     *  - page: 0-based (default 0)
     *  - size: page size (default 10)
     *  - sort: "createdAt,desc" or "salary,asc" etc. (default "createdAt,desc")
     */
    @GetMapping("/search")
    public ResponseEntity<Page<JobResponse>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) JobType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        return ResponseEntity.ok(service.search(q, category, location, type, page, size, sort));
    }

    /**
     * List jobs posted by the current employer/admin (requires EMPLOYER or ADMIN).
     * Pagination params are optional.
     */
    @GetMapping("/my")
    public ResponseEntity<Page<JobResponse>> myJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(service.myJobs(page, size));
    }
}
