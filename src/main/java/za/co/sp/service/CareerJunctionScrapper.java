package za.co.sp.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import za.co.sp.entity.Job;
import za.co.sp.repository.JobRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
public record CareerJunctionScrapper(@Value("${career.junction.base.url}") String baseUrl,
                                     JobRepository jobRepository) implements WebScrapper {
    private String generateUrl(String title, int page) {
        String url = String.format("%s/jobs/results?keywords=%s", baseUrl, title);
        if (page > 0) {
            url = String.format("%s&page=%d", url, page);
        }
        return url;
    }

    private String extractDescription(String link) {

        try {

            String url = String.format("%s%s", baseUrl, link);
            log.info("Extracting job description from [URL]  : {} ", url);
            Document document = Jsoup.connect(url).get();

            Elements jobDescription = document.getElementsByClass("job-details-description");
            return jobDescription.get(0).text();
        } catch (MalformedURLException | UnsupportedMimeTypeException e) {
            log.error("Something went wrong, issue possibly to be mimetype or url.");
            log.error("Breakdown [Message]           : {}", e.getMessage());
        } catch (HttpStatusException e) {
            log.error("Request unsuccessful, getting description");
            log.error("Breakdown [Status Url]        : {}", e.getUrl());
            log.error("Breakdown [Status Code]       : {}", e.getStatusCode());
            log.error("Breakdown [Status Message]    : {}", e.getMessage());
        } catch (IOException e) {
            log.error("Something went wrong terribly when extracting description!!!!");
            log.error("Breakdown [Error Messages]    : {}", e.getMessage());
            log.error("Error                         : ", e);

        }
        return null;

    }


    @Override
    public List<Job> scrape(String jobTitle) {

        log.info("Starting scraping....");
        log.info("Searching for job title: {}", jobTitle);

        List<Job> jobs = new ArrayList<>();
        //
        List<String> scrappedJobsLinks = jobRepository.findAll().stream().map(Job::getLink).toList();


        int page = 1;
        boolean run = true;
        int FOUND_COUNT_BEFORE_TERMINATE = 0;
        while (run) {

            try {
                String url = generateUrl(jobTitle, page);
                log.info("Scrapping Url  : {}", url);
                log.info("Scrapping page : {}", url);

                Document document = Jsoup.connect(url).get();
                Elements elementsByClass = document.getElementsByClass("module-content");

                for (Element element : elementsByClass) {
                    if (!element.getElementsByClass("job-result-title").isEmpty()) {
                        String link = element.getElementsByClass("job-result-title").get(0).getElementsByTag("a").get(0).attr("href");
                        String title = element.getElementsByClass("job-result-title").get(0).getElementsByTag("a").get(0).text();
                        String salary = element.getElementsByClass("salary").text();
                        String datePosted = element.getElementsByClass("updated-time").text();
                        String expireDate = element.getElementsByClass("expires").text();
                        String location = element.getElementsByClass("location").text();
                        String type = element.getElementsByClass("position").text();
                        String companyName;
                        try {
                            companyName = element.getElementsByClass("job-result-title").get(0).getElementsByTag("a").get(1).text();
                        } catch (Exception e) {
                            companyName = "";
                        }

                        String fullLink = baseUrl + link;
                        expireDate = extractExpireDate(expireDate);

                        if (!scrappedJobsLinks.contains(fullLink)) {
                            String description = extractDescription(link);
                            jobs.add(new Job(null, title, fullLink, extractDate(datePosted), extractDate(datePosted).plusDays(Long.parseLong(expireDate)), location, extractContractType(type), salary, description, companyName));
                        } else {
                            FOUND_COUNT_BEFORE_TERMINATE++;
                            if (FOUND_COUNT_BEFORE_TERMINATE >= 3) {
                                log.info("JOB Already scrapped [{}] Max FOUND_COUNT_BEFORE_TERMINATE reached [{}] terminating scrapping process...", link, FOUND_COUNT_BEFORE_TERMINATE);
                                run = false;
                                break;
                            }
                        }

                    } else {
                        if (element.getElementsByClass("alert-warning").isEmpty()) {
                            log.info("Reached the last page");
                            run = false;
                        }
                    }
                }
                page++;
                Thread.sleep(3);
            } catch (MalformedURLException | UnsupportedMimeTypeException e) {
                log.error("Something went wrong, issue possibly to be mimetype or url");
                log.error("Breakdown [Message]          : {}", e.getMessage());
                log.error("Error: ", e);
            } catch (HttpStatusException e) {
                log.error("Request unsuccessful");
                log.error("Breakdown [Status Url]       : {}", e.getUrl());
                log.error("Breakdown [Status Code]      : {}", e.getStatusCode());
                log.error("Breakdown [Status Message]   : {}", e.getMessage());
                log.error("Error                        : ", e);
            } catch (Exception e) {
                log.error("Something went wrong terribly!!!!");
                log.error("Breakdown [Error Message]    : {}", e.getMessage());
                log.error("Error                        : ", e);
            }
        }


        log.info("Finished scraping....");
        return jobs;
    }

    private String extractExpireDate(String expireDate) {
        String[] s = expireDate.split(" ");
        return s[s.length - 2];
    }

    private String extractContractType(String type) {
        if (type.contains("Permanent") || type.contains("permanent")) {
            return "Permanent";
        } else {
            return "Contract";
        }
    }


    private static LocalDate extractDate(String date) {
        if (date.startsWith("Posted")) {
            date = date.substring(6);
        }
        date = date.strip();
        // Define formatter matching the input format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
        // Parse string to LocalDate
        return LocalDate.parse(date, formatter);
    }

}
