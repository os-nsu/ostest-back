
package ru.nsu.ostest.domain.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.ostest.adapter.out.persistence.entity.group.Group;
import ru.nsu.ostest.domain.repository.GroupRepository;

@AllArgsConstructor
@Service
public class GroupService {
    private final GroupRepository groupRepository;

    public Group findGroupByName(String name) {
        return groupRepository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException("Couldn't find group with name: " + name));
    }

    public Group findGroupById(Long id) {
        return groupRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Couldn't find group with id: " + id));
    }

}
