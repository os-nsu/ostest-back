package ru.nsu.ostest.domain.repository;

import org.springframework.data.repository.CrudRepository;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;

import java.util.Optional;


public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
