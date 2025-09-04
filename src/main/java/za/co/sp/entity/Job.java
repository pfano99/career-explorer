package za.co.sp.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "job")
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    @Id
    @GeneratedValue
    private Long id;

    private String title;

    @Column(unique = true)
    private String link;

    @Column(name = "date_posted")
    private String datePosted;

    @Column(name = "expire_date")
    private String expireDate;
    private String area;
    @Column(name = "job_type")
    private String jobType;
    private String salary;

    private String description;

}
