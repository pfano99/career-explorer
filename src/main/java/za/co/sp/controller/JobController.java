package za.co.sp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.sp.entity.Job;
import za.co.sp.entity.JobTitle;
import za.co.sp.model.MatchRequest;
import za.co.sp.model.MatchRespond;
import za.co.sp.service.JobService;
import za.co.sp.service.MatchingService;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping
public record JobController(JobService jobService, MatchingService matchingService) {


    @Data
    public static class Request{
        List<Long> data;
    }

    @GetMapping("/")
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobService.retrieveAllJobs());
    }

    @GetMapping("/matched")
    public ResponseEntity<List<Job>> getMatchedJobs(@RequestBody Request request) {
        return ResponseEntity.ok(jobService.retrieveAllMatchedJobs(request));
    }

    @GetMapping("/job-title")
    public ResponseEntity<List<JobTitle>> getJobTitles() {
        return ResponseEntity.ok(jobService.retrieveJobTitles());
    }

    @PostMapping("/save/job-title")
    public ResponseEntity<JobTitle> saveJobTitle(@RequestParam String jobTitle) {
        log.trace("JobController.saveJobTitle");
        log.info("Request param for job title: [{}]", jobTitle);
        return ResponseEntity.ok(jobService.saveNewJobTitle(jobTitle));
    }

    @PostMapping("/match")
    public ResponseEntity<List<MatchRespond>> match(@RequestBody MatchRequest matchRequest) throws JsonProcessingException {
        return ResponseEntity.ok(matchingService.match(matchRequest));

    }

    @GetMapping("/scrape")
    public ResponseEntity<List<Job>> scrape(@RequestParam String jobTitle) throws IOException {
        return ResponseEntity.ok(jobService.scrape(jobTitle));
    }


}
