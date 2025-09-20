package za.co.sp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class LLMResponse {
    @Data
    public static class Choice{
        @JsonProperty("finish_reason")
        private String finishReason;
        private int index;
        private Message message;
    }

    private List<Choice> choices;


}
