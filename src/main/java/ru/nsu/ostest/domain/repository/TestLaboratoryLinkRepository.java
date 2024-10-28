package ru.nsu.ostest.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.ostest.adapter.out.persistence.entity.test.TestLaboratoryLink;

@Repository
public interface TestLaboratoryLinkRepository extends JpaRepository<TestLaboratoryLink, Long> {

    TestLaboratoryLink findByLaboratoryIdAndTestId(Long testLaboratoryId, Long testId);
}