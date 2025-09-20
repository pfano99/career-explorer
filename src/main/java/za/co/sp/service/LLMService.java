package za.co.sp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import za.co.sp.model.LLMRequest;
import za.co.sp.model.LLMResponse;
import za.co.sp.model.Message;

import java.util.List;

@Service
public record LLMService(RestTemplate restTemplate, @Value("${llm.base.url}") String modelsUrl) {

    public LLMResponse prompt(String jobDescription, String candidateSkills) {

        String prompt = """
        Compare the following job description and the candidate’s skills.
        
        Respond ONLY in valid JSON with the following format:
        {
          "overlapping_skills": ["..."],
          "missing_skills": ["..."],
          "match_score": 0-100,
          "explanation": "..."
        }

        Job Description:
        """ + jobDescription + "\n\nCandidate Skills:\n" + candidateSkills;


        Message systemMessage = new Message();
        systemMessage.setContent("You are an expert recruiter.");
        systemMessage.setRole("system");

        Message userMessage = new Message();
        userMessage.setContent(prompt);
        userMessage.setRole("user");


        LLMRequest llmRequest = new LLMRequest();
        llmRequest.setModel("ai/gemma3-qat:latest");
        llmRequest.setMessages(List.of(systemMessage, userMessage));

        return restTemplate.postForEntity(modelsUrl, llmRequest, LLMResponse.class).getBody();
    }


}
