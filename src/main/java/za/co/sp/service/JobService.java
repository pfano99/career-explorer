package za.co.sp.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import za.co.sp.entity.Job;
import za.co.sp.repository.JobRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@Slf4j
@Service
public record JobService(@Value("${career.junction.base.url}") String baseUrl, JobRepository jobRepository) {


    public List<Job> getAll() {
        return jobRepository.findAll();
    }

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
            System.out.println("description url = " + url);
            Document document = Jsoup.connect(url).get();

            Elements jobDescription = document.getElementsByClass("job-details-description");
            return jobDescription.get(0).text();
        } catch (MalformedURLException | UnsupportedMimeTypeException e) {
            System.err.println("Something went wrong, issue possibly to be mimetype or url");
            System.err.println("Breakdown [Message]: " + e.getMessage());
        } catch (HttpStatusException e) {
            System.err.println("Request unsuccessful, getting description");
            System.err.println("Breakdown [Status Url]: " + e.getUrl());
            System.err.println("Breakdown [Status Code]: " + e.getStatusCode());
            System.err.println("Breakdown [Status Message]: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Something went wrong terribly!!!!");
        }
        return null;

    }

    public void scrape(String searchTitle) {

        try {
            log.info("Scrapping starting....");
            for (int i = 0; i < 10; i++) {
                String url = generateUrl(searchTitle, i);
                log.info("Url: {}", url);
                log.info("Page: {}", url);
                Document document = Jsoup.connect(url).get();


                Elements elementsByClass = document.getElementsByClass("module-content");
                for (Element element : elementsByClass) {

                    if (!element.getElementsByClass("job-result-title").isEmpty()) {
                        String link = element.getElementsByClass("job-result-title").get(0).getElementsByTag("a").get(0).attr("href");
                        String title = element.getElementsByClass("job-result-title").get(0).getElementsByTag("a").get(0).text();
                        String salary = element.getElementsByClass("salary").text();
                        String datePosted = element.getElementsByClass("updated-time").text();
                        String expireDate = element.getElementsByClass("updated-time").text();
                        String location = element.getElementsByClass("location").text();
                        String type = element.getElementsByClass("position").text();

                        String description = extractDescription(link);

                        Job job = new Job(null, title, link, datePosted, expireDate, location, type, salary, description);
                        try {
                            jobRepository.save(job);
                        } catch (DataIntegrityViolationException e) {
                            log.warn("Duplicate entry found, Skipping entry!!!");
                        }
                    }
                }
                Thread.sleep(3);
            }

            //Todo: Use constant/static MAX_PAGE_LOOK_UP

        } catch (MalformedURLException | UnsupportedMimeTypeException e) {
            System.err.println("Something went wrong, issue possibly to be mimetype or url");
            System.err.println("Breakdown [Message]: " + e.getMessage());
        } catch (HttpStatusException e) {
            System.err.println("Request unsuccessful");
            System.err.println("Breakdown [Status Url]: " + e.getUrl());
            System.err.println("Breakdown [Status Code]: " + e.getStatusCode());
            System.err.println("Breakdown [Status Message]: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Something went wrong terribly!!!!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


}
