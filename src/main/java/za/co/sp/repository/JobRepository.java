package za.co.sp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import za.co.sp.entity.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
}
