package ru.nsu.ostest.domain.service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nsu.ostest.adapter.in.rest.model.user.RoleEnum;
import ru.nsu.ostest.adapter.out.persistence.entity.user.Role;
import ru.nsu.ostest.domain.repository.RoleRepository;
import ru.nsu.ostest.security.exceptions.NotFoundException;

import java.util.Optional;

@AllArgsConstructor
@Service
@Slf4j
public class RoleService {
    private final RoleRepository roleRepository;

    public Role findRole(RoleEnum name) {
        Optional<Role> roleOptional = roleRepository.findByName(name.toString());
        if (roleOptional.isEmpty()) {
            log.error("Couldn't find role [{}]", name);
            throw new NotFoundException("Couldn't find role " + name);
        }
        return roleOptional.get();
    }
}
