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

    public static Specification<Session> byUserId(Long userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.equal(root.get("student.id"), userId),
                criteriaBuilder.equal(root.get("teacher.id"), userId)
        );
    }
}
