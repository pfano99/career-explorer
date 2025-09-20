package za.co.sp.model;

import lombok.Data;

import java.util.List;

@Data
public class LLMRequest {

    private String model;

    private List<Message> messages;

}
