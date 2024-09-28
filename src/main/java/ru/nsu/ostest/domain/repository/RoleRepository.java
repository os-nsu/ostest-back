package ru.nsu.ostest.domain.repository;

import org.springframework.data.repository.CrudRepository;
import ru.nsu.ostest.adapter.out.persistence.entity.user.Role;

import java.util.Optional;


public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByName(String roleName);
}
