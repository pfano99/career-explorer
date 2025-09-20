package za.co.sp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.sp.entity.JobTitle;

@Repository
public interface JobTitleRepository extends JpaRepository<JobTitle,Long> {
}
