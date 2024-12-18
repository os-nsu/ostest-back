package ru.nsu.ostest.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.ostest.adapter.out.persistence.entity.session.Attempt;
import ru.nsu.ostest.domain.common.enums.AttemptStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, Long> {
    Optional<Attempt> findById(UUID id);

    List<Attempt> findAllByStatusAndCreatedAtBefore(AttemptStatus status, OffsetDateTime threshold);

    Optional<Attempt> findFirstByStatusOrderByCreatedAtAsc(AttemptStatus status);
}
