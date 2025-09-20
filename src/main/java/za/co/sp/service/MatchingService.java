package za.co.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.sp.entity.Job;
import za.co.sp.model.LLMResponse;
import za.co.sp.model.MatchRequest;
import za.co.sp.model.MatchRespond;
import za.co.sp.utils.LocationUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public record MatchingService(JobService jobService, LLMService llmService, ObjectMapper objectMapper) {


    private boolean locationFilter(List<String> candidatePreferenceLocation, String jobLocation) {
        jobLocation = jobLocation.strip();
        if ("south africa".equalsIgnoreCase(jobLocation)) {
            return true;
        }
        if (candidatePreferenceLocation.size() == 9) {
            return true;
        }
        if (candidatePreferenceLocation.contains(jobLocation)) {
            return true;
        }
        if (candidatePreferenceLocation.contains(LocationUtils.CITY_TO_PROVINCE.get(jobLocation))) {
            return true;
        }

        int index = jobLocation.indexOf('(');
        if (index != -1 && LocationUtils.CITY_TO_PROVINCE.get(jobLocation.substring(0, index).strip()) != null) {
            return true;
        }
        index = jobLocation.indexOf("cbd");
        if (index != -1 && LocationUtils.CITY_TO_PROVINCE.get(jobLocation.substring(0, index).strip()) != null) {
            return true;
        }
        log.info("Location filter for JOB LOCATION: [{}] failed ", jobLocation);
        return false;
    }

    private boolean isContractFilter( List<String> candidatePreferenceContract, String jobContractType ) {
        jobContractType = jobContractType.strip().toLowerCase();
        if (!jobContractType.isEmpty() && !List.of("permanent", "contract").contains(jobContractType)) {
            log.info("Invalid job contract type: [{}]", jobContractType);
            return true;
        }
        return candidatePreferenceContract.stream().map(String::toLowerCase).toList().contains(jobContractType);
    }

    private boolean candidatePreferenceFilter(Job job, MatchRequest matchRequest) {
        log.info("Running candidate preference filter");
        //Todo: add inspection for salary
        //Todo: add inspection for work-type[Remote, Hybrid, etc..]
        return locationFilter(matchRequest.getLocations(), job.getArea()) && isContractFilter(matchRequest.getContracts(), job.getJobType());
    }

    private boolean TFIDFFilter(Job job, MatchRequest matchRequest) {
        log.info("Running term frequency inverse document frequency filter");
        return true;
    }


    public List<MatchRespond> match(MatchRequest requestPayload) throws JsonProcessingException {

        List<Job> jobs = jobService.retrieveAllJobs().reversed().stream().filter(job -> candidatePreferenceFilter(job, requestPayload)).filter(job -> TFIDFFilter(job, requestPayload)).toList();
        String skills = objectMapper.writeValueAsString(requestPayload.getSkills());
        log.info("Job filters successfully applied");
        log.info("Job that match candidate count: {}", jobs.size());
        List<MatchRespond> matchResponds = new ArrayList<>();
        for (Job job : jobs) {
            LLMResponse response = llmService.prompt(job.getDescription(), skills);
            String content = response.getChoices().getFirst().getMessage().getContent();
            if (content.strip().startsWith("```json")) {
                content = content.strip().substring("```json".length()).replaceAll("```", "");
                JsonNode jsonNode = objectMapper.readTree(content);
                float matchScore = jsonNode.get("match_score").floatValue();
                if (matchScore > 50) {
                    matchResponds.add(new MatchRespond(job.getId(), matchScore));
                    log.info("Candidate job match found [JOB]: <{}, {}> AT [SCORE]: {}", job.getId(), job.getTitle(), matchScore);
                } else {
                    log.info("Candidate job match score is too low for [JOB]:<{}, {}> at [SCORE]: {}", job.getId(), job.getTitle(), matchScore);
                }
            }
        }
        log.info("MatchResponds successfully applied");
        return matchResponds;
    }


    public static void main(String[] args) throws JsonProcessingException {
        String s = "```json\n{\n  \"overlapping_skills\": [\"Java\", \"Spring Boot\", \"Docker\"],\n  \"missing_skills\": [\"Kubernetes\", \"MySQL\"],\n  \"match_score\": 85,\n  \"explanation\": \"The candidate possesses strong alignment with the core requirements of the job description – Java, Spring Boot, and Docker expertise. While Kubernetes and MySQL aren't explicitly mentioned, the candidate has experience with Docker, a critical component.  The match score is high due to the significant overlap in key technologies, but there's a slight deduction for the missing skills. A score of 85 reflects that the candidate is a very good fit, but further exploration into their Kubernetes and MySQL knowledge would be beneficial.\"\n}\n```";
        System.out.println("s = " + s);
        if (s.strip().startsWith("```json")) {
            String con = s.strip().substring("```json".length()).replaceAll("```", "");

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode jsonNode = mapper.readTree(con);
            System.out.println("jsonNode.get(\"match_score\").asDouble() = " + jsonNode.get("match_score").asDouble());
        }
    }


//
//    public List<MatchRespond> match(MatchRequest requestPayload) {
//
//        String jobDescription = "We are hiring a backend developer with Java, Spring Boot, and Docker experience.";
//        String userSkills = "Java, Spring Boot, Kubernetes, MySQL, Docker, REST APIs";
//
//        String prompt = """
//        Compare the following job description and the candidate’s skills.
//
//        Respond ONLY in valid JSON with the following format:
//        {
//          "overlapping_skills": ["..."],
//          "missing_skills": ["..."],
//          "match_score": 0-100,
//          "explanation": "..."
//        }
//
//        Job Description:
//        """ + jobDescription + "\n\nCandidate Skills:\n" + userSkills;
//
//
//        Message systemMessage = new Message();
//        systemMessage.setContent("You are an expert recruiter.");
//        systemMessage.setRole("system");
//
//        Message userMessage = new Message();
//        userMessage.setContent(prompt);
//        userMessage.setRole("user");
//
//
//        LLMRequest llmRequest = new LLMRequest();
//        llmRequest.setModel("ai/gemma3-qat:latest");
//        llmRequest.setMessages(List.of(systemMessage, userMessage));
//
//        ResponseEntity<LLMResponse> stringResponseEntity = restTemplate.postForEntity(modelsUrl, llmRequest, LLMResponse.class);
//        System.out.println("stringResponseEntity.getStatusCode() = " + stringResponseEntity.getStatusCode());
//        System.out.println("stringResponseEntity.getBody() = " + stringResponseEntity.getBody());
//
//        return Collections.EMPTY_LIST;
//    }

}
