package ru.nsu.ostest.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.nsu.ostest.adapter.out.persistence.entity.laboratory.Laboratory;

import java.util.List;

@Repository
public interface LaboratoryRepository extends JpaRepository<Laboratory, Long> {

    Laboratory findByName(String name);

    @Query("SELECT l FROM Laboratory l WHERE (:isHidden IS NULL OR l.isHidden = :isHidden) " +
            "AND (:semesterNumber IS NULL OR l.semesterNumber = :semesterNumber)")
    List<Laboratory> findLaboratoriesByIsHiddenAndSemesterNumber( Boolean isHidden, Byte semesterNumber);
}