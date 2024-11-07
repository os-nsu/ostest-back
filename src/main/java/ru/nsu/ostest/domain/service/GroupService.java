package ru.nsu.ostest.domain.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupDto;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupEditionRequestDto;
import ru.nsu.ostest.adapter.mapper.GroupMapper;
import ru.nsu.ostest.adapter.out.persistence.entity.group.Group;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;
import ru.nsu.ostest.domain.exception.validation.DuplicateGroupNameException;
import ru.nsu.ostest.domain.exception.validation.GroupNotFoundException;
import ru.nsu.ostest.domain.repository.GroupRepository;
import ru.nsu.ostest.domain.repository.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@AllArgsConstructor
@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;
    private final UserRepository userRepository;

    public Group findGroupByName(String name) {
        return groupRepository.findByName(name);
    }

    @Transactional
    public GroupDto create(GroupCreationRequestDto request) {
        checkIfDuplicatedName(request.name());

        Group group = groupMapper.groupCreationRequestDtoToGroup(request);

        group = groupRepository.save(group);
        log.info("Entity group saved: {}", group);

        return groupMapper.groupToGroupDto(group);
    }

    public GroupDto getGroup(Long id) {
        return groupRepository.findById(id)
                .map(groupMapper::groupToGroupDto)
                .orElseThrow(() -> GroupNotFoundException.notFoundGroupWithId(id));
    }

    public Page<GroupDto> getAllGroups(Pageable pageRequest) {
        return groupRepository.findAll(pageRequest)
                .map(groupMapper::groupToGroupDto);
    }

    @Transactional
    public GroupDto update(GroupEditionRequestDto groupEditionRequestDto) {
        checkIfDuplicatedName(groupEditionRequestDto.name(), groupEditionRequestDto.id());

        Group group = groupRepository.findById(groupEditionRequestDto.id())
                .orElseThrow(() -> GroupNotFoundException.notFoundGroupWithId(groupEditionRequestDto.id()));
        groupMapper.groupEditionRequestDtoToGroup(group, groupEditionRequestDto);

        log.info("Group named {} replaced by group {}}", groupEditionRequestDto.name(), group.toString());

        Set<Long> deleteUsersIds = groupEditionRequestDto.deleteUsers();
        if (CollectionUtils.isNotEmpty(deleteUsersIds)) {
            deleteUsersFromGroup(group, deleteUsersIds);
        }

        Set<Long> addUsersIds = groupEditionRequestDto.addUsers();
        if (CollectionUtils.isNotEmpty(addUsersIds)) {
            addUsersToGroup(group, addUsersIds);
        }

        return groupMapper.groupToGroupDto(group);
    }

    private void addUsersToGroup(Group group, Set<Long> addUsersIds) {
        Set<User> users = group.getUsers();
        List<Long> usersIds = users.stream()
                .map(User::getId)
                .toList();
        Set<Long> idsToAdd = addUsersIds.stream()
                .filter(id -> !usersIds.contains(id))
                .collect(Collectors.toSet());
        for (Long id : idsToAdd) {
            User userToAdd = userRepository.getReferenceById(id);
            users.add(userToAdd);
        }
        groupRepository.flush();
    }

    private void deleteUsersFromGroup(Group group, Set<Long> deleteUsersIds) {
        Set<User> users = group.getUsers();
        List<User> usersToDelete = users.stream()
                .filter(u -> deleteUsersIds.contains(u.getId()))
                .toList();
        users.removeAll(usersToDelete);
        groupRepository.flush();
    }

    @Transactional
    public void delete(Long id) {
        groupRepository.deleteById(id);
    }

    private void checkIfDuplicatedName(String name, Long exceptedId) {
        Group group = groupRepository.findByName(name);
        if (group != null && !group.getId().equals(exceptedId)) {
            throw DuplicateGroupNameException.of(name);
        }
    }

    private void checkIfDuplicatedName(String name) {
        if (groupRepository.findByName(name) != null) {
            throw DuplicateGroupNameException.of(name);
        }
    }

    public Group findGroupById(Long id) {
        return groupRepository.findById(id).orElseThrow(
                () -> GroupNotFoundException.notFoundGroupWithId(id));
    }

}
