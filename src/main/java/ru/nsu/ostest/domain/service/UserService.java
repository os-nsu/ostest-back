package ru.nsu.ostest.domain.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;
import ru.nsu.ostest.domain.repository.UserRepository;
import ru.nsu.ostest.security.exceptions.NotFoundException;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Couldn't find user with id: " + id));
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new NotFoundException("Couldn't find user with username: " + username));
    }
}
