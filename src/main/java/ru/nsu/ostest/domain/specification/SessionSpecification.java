package ru.nsu.ostest.domain.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.nsu.ostest.adapter.out.persistence.entity.session.Session;

public class SessionSpecification {
    public static Specification<Session> byStudentIdAndLaboratoryId(Long studentId, Long laboratoryId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("student.id"), studentId),
                criteriaBuilder.equal(root.get("laboratory.id"), laboratoryId)
        );
    }
}
