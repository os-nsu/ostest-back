package ru.nsu.ostest.domain.repository;

import org.springframework.data.repository.CrudRepository;
import ru.nsu.ostest.adapter.out.persistence.entity.group.Group;

import java.util.Optional;

public interface GroupRepository extends CrudRepository<Group, Long> {
    Optional<Group> findByName(String groupName);
}