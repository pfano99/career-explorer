package za.co.sp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@Table(name = "job_title")
@NoArgsConstructor
@AllArgsConstructor
public class JobTitle {

    @Id
    @GeneratedValue(generator="job_title_seq")
    @SequenceGenerator(name="job_title_seq",sequenceName="job_title_seq")
    private Long id;

    private String title;

}
