package ru.nsu.ostest.domain.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupDto;
import ru.nsu.ostest.adapter.mapper.GroupMapper;
import ru.nsu.ostest.adapter.out.persistence.entity.group.Group;
import ru.nsu.ostest.domain.exception.DuplicateTestNameException;
import ru.nsu.ostest.domain.repository.GroupRepository;

@Slf4j
@AllArgsConstructor
@Service
public class GroupService {
    private static final String GROUP_CREATED_MESSAGE_TEMPLATE = "Entity group created: {}";
    private static final String GROUP_SAVED_MESSAGE_TEMPLATE = "Entity group saved: {}";
    private static final String GROUP_FOUND_MESSAGE_TEMPLATE = "Found group with id = {}";
    private static final String GROUPS_FOUND_MESSAGE_TEMPLATE = "N = {} groups found.";
    private static final String GROUP_DELETED_MESSAGE_TEMPLATE = "Group with id = {} deleted";
    private static final String GROUP_NOT_FOUND_MESSAGE_TEMPLATE = "Group not found.";
    private static final String GROUP_REPLACED_MESSAGE_TEMPLATE = "Group named {} replaced by group {}}";
    private static final String FILE_READ_MESSAGE = "File read.";
    private static final String DUPLICATED_NAME_MESSAGE = "A group with this name already exists.";
    private static final String FILE_READING_FAILED_MESSAGE = "Failed to read file.";

    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;

    public Group findGroupByName(String name) {
        return groupRepository.findByName(name);
    }

    @Transactional
    public GroupDto create(GroupCreationRequestDto request) {
        checkIfDuplicatedName(request.name());

        Group group = groupMapper.groupCreationRequestDtoToGroup(request);
        log.debug(GROUP_CREATED_MESSAGE_TEMPLATE, group);

        group = groupRepository.save(group);
        log.info(GROUP_SAVED_MESSAGE_TEMPLATE, group);

        return groupMapper.groupToGroupDto(group);
    }

    //-----------------------------------------------------------------------------

//    public Group findGroupByName(String name) {
//        return groupRepository.findByName(name).orElseThrow(
//                () -> new NotFoundException("Couldn't find group with name: " + name));
//    }

    private void checkIfDuplicatedName(String name, Long exceptedId) {
        Group group = groupRepository.findByName(name);
        if (group != null && !group.getId().equals(exceptedId)) {
            log.error(DUPLICATED_NAME_MESSAGE);
            throw DuplicateTestNameException.of(name);
        }
    }

    private void checkIfDuplicatedName(String name) {
        if (groupRepository.findByName(name) != null) {
            log.error(DUPLICATED_NAME_MESSAGE);
            throw DuplicateTestNameException.of(name);
        }
    }

}
