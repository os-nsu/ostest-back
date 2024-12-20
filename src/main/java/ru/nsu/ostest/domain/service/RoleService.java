package ru.nsu.ostest.domain.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nsu.ostest.adapter.in.rest.model.user.role.RoleEnum;
import ru.nsu.ostest.adapter.out.persistence.entity.user.Role;
import ru.nsu.ostest.domain.exception.validation.RoleNotFoundException;
import ru.nsu.ostest.domain.repository.RoleRepository;

import java.util.Optional;

@AllArgsConstructor
@Service
@Slf4j
public class RoleService {
    private final RoleRepository roleRepository;

    public Role findRole(RoleEnum name) {
        Optional<Role> roleOptional = roleRepository.findByRoleName(name.toString());
        if (roleOptional.isEmpty()) {
            throw RoleNotFoundException.notFoundRoleWithName(name.toString());
        }
        return roleOptional.get();
    }
}
