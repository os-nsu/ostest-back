package ru.nsu.ostest.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.ostest.adapter.out.persistence.entity.session.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long>{

    Session getSessionByStudentIdAndLaboratoryId(Long studentId, Long laboratoryId);

    Page<Session> getSessionByStudentIdOrTeacherId(Long studentId, Long teacherId, Pageable pageable);
}
