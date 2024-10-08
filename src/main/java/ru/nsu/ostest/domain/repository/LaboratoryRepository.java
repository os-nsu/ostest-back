package ru.nsu.ostest.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.nsu.ostest.adapter.out.persistence.entity.laboratory.Laboratory;

@Repository
public interface LaboratoryRepository extends JpaRepository<Laboratory, Long>, JpaSpecificationExecutor<Laboratory> {

    Laboratory findByName(String name);
}