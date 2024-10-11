package ru.nsu.ostest.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.ostest.adapter.out.persistence.entity.test.Test;

@Repository
public interface TestRepository extends JpaRepository<Test, Long>{

    Test findByName(String name);

}