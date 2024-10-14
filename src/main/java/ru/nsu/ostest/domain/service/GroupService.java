
package ru.nsu.ostest.domain.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupDto;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupEditionRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupSearchRequestDto;
import ru.nsu.ostest.adapter.mapper.GroupMapper;
import ru.nsu.ostest.adapter.out.persistence.entity.group.Group;
import ru.nsu.ostest.domain.exception.DuplicateTestNameException;
import ru.nsu.ostest.domain.repository.GroupRepository;
import ru.nsu.ostest.security.exceptions.NotFoundException;

import java.util.List;


@Slf4j
@AllArgsConstructor
@Service
public class GroupService {
    private static final String GROUP_NOT_FOUND_MESSAGE_TEMPLATE = "Group not found.";
    private static final String DUPLICATED_NAME_MESSAGE = "A group with this name already exists.";

    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;

    public Group findGroupByName(String name) {
        return groupRepository.findByName(name);
    }

    @Transactional
    public GroupDto create(GroupCreationRequestDto request) {
        checkIfDuplicatedName(request.name());

        Group group = groupMapper.groupCreationRequestDtoToGroup(request);

        group = groupRepository.save(group);
        log.info("Entity group saved: {}", group.toString());

        return groupMapper.groupToGroupDto(group);
    }

    public GroupDto getGroup(Long id) {
        GroupDto groupDto = groupMapper.groupToGroupDto(groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(GROUP_NOT_FOUND_MESSAGE_TEMPLATE)));

        return groupDto;
    }

    public List<GroupSearchRequestDto> getAllGroups() {
        List<GroupSearchRequestDto> list = groupMapper.groupsToGroupDtoList(groupRepository.findAll());

        return list;
    }

    @Transactional
    public GroupDto update(GroupEditionRequestDto groupEditionRequestDto) {
        checkIfDuplicatedName(groupEditionRequestDto.name(), groupEditionRequestDto.id());

        Group group = groupRepository.findById(groupEditionRequestDto.id())
                .orElseThrow(() -> new EntityNotFoundException(GROUP_NOT_FOUND_MESSAGE_TEMPLATE));
        groupMapper.groupEditionRequestDtoToGroup(group, groupEditionRequestDto);

        log.info("Group named {} replaced by group {}}", groupEditionRequestDto.name(), group.toString());
        return groupMapper.groupToGroupDto(group);
    }

    @Transactional
    public void delete(Long id) {
        groupRepository.deleteById(id);
    }


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

    public Group findGroupById(Long id) {
        return groupRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Couldn't find group with id: " + id));
    }

}
