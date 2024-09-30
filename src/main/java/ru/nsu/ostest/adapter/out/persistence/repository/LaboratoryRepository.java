package ru.nsu.ostest.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.ostest.adapter.out.persistence.entity.laboratory.Laboratory;

@Repository
public interface LaboratoryRepository extends JpaRepository<Laboratory, Long> {

    Laboratory findByName(String name);
}
