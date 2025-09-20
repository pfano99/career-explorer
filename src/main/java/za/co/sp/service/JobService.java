package za.co.sp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import za.co.sp.controller.JobController;
import za.co.sp.entity.Job;
import za.co.sp.entity.JobTitle;
import za.co.sp.repository.JobRepository;
import za.co.sp.repository.JobTitleRepository;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public record JobService(JobRepository jobRepository, JobTitleRepository jobTitleRepository, WebScrapper webScrapper) {


    public List<Job> retrieveAllJobs() {
        return jobRepository.findAll();
    }

    public List<JobTitle> retrieveJobTitles() {
        return jobTitleRepository.findAll();
    }

    public JobTitle saveNewJobTitle(String jobTitle) {
        return jobTitleRepository.save(new JobTitle(null, jobTitle));
    }

    public List<Job> scrape(String searchTitle) throws IOException {
        saveJobTitle(searchTitle);
        List<Job> newlyScrapedJobs = webScrapper.scrape(searchTitle);
        return jobRepository.saveAll(newlyScrapedJobs);
    }


    public void saveJobTitle(String jobTitle) {
        try{
            log.info("Saving job title [{}] to database", jobTitle);
            jobTitleRepository.save(new JobTitle(null, jobTitle.toLowerCase().strip()));
        }catch (DataIntegrityViolationException e){
            log.info("Job title already exists       : [{}]", jobTitle);
            log.info("Skipping job title             : [{}]", jobTitle);
        }catch (Exception e){
            log.error("Failed to save Job Title      : [{}]", jobTitle);
            log.error("Breakdown [Status Message]    :  {}", e.getMessage());
            log.error("Error                         :  ", e);

        }

    }



    public List<Job> retrieveAllMatchedJobs(JobController.Request request) {
        return jobRepository.findAllById(request.getData());
    }
}
