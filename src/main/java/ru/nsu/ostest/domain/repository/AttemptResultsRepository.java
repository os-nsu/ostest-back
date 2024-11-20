package ru.nsu.ostest.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.ostest.adapter.out.persistence.entity.session.AttemptResults;

@Repository
public interface AttemptResultsRepository extends JpaRepository<AttemptResults, Long> {
}
