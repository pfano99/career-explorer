package za.co.sp.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "job")
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    @Id
    @GeneratedValue(generator="job_seq")
    @SequenceGenerator(name="job_seq",sequenceName="job_seq")
    private Long id;

    private String title;

    @Column
    private String link;

    @Column(name = "date_posted")
    private LocalDate datePosted;

    @Column(name = "expire_date")
    private LocalDate expireDate;

    private String area;

    @Column(name = "job_type")
    private String jobType;

    private String salary;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Column(name = "company_name")
    private String companyName;

}
