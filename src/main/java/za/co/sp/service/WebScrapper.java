package za.co.sp.service;

import za.co.sp.entity.Job;

import java.io.IOException;
import java.util.List;

public interface WebScrapper {

    List<Job> scrape(String jobTitle) throws IOException;

}
