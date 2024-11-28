package ru.nsu.ostest.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.nsu.ostest.adapter.out.persistence.entity.user.StudentRating;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<StudentRating, Long> {

    @Query(value = "SELECT * FROM student_lab_ratings  LIMIT :limit", nativeQuery = true)
    List<StudentRating> findTopNStudents(@Param("limit") int limit);
}
