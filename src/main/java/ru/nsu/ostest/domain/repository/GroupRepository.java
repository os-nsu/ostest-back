package ru.nsu.ostest.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.ostest.adapter.out.persistence.entity.group.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    Group findByName(String name);

}