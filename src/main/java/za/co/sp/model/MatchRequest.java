package za.co.sp.model;

import lombok.Data;

import java.util.List;

@Data
public class MatchRequest {
    private String jobTitle;
    private List<Skill> skills;
    private List<String> locations;
    private List<String> contracts;
    private String workType;
    private Long minSalary;
}
