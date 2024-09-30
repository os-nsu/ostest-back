package ru.nsu.ostest.domain.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.nsu.ostest.adapter.out.persistence.entity.laboratory.Laboratory;

public class LaboratorySpecification {

    public static Specification<Laboratory> byIsHiddenAndSemesterNumber(Boolean isHidden, Integer semesterNumber) {
        return (root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();

            if (isHidden != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("isHidden"), isHidden));
            }

            if (semesterNumber != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("semesterNumber"), semesterNumber));
            }

            return predicates;
        };
    }
}