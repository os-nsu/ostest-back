package ru.nsu.ostest.adapter.in.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.nsu.ostest.adapter.in.rest.model.filter.SearchRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.group.*;
import ru.nsu.ostest.domain.service.GroupService;
import ru.nsu.ostest.security.annotations.AdminOnlyAccess;
import ru.nsu.ostest.security.annotations.AdminOrTeacherAccess;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/group")
public class GroupController {
    private final GroupService groupService;

    @AdminOnlyAccess
    @PostMapping
    public GroupDto createGroup(@RequestBody GroupCreationRequestDto request) {
        return groupService.create(request);
    }

    @AdminOrTeacherAccess
    @GetMapping("/{id}")
    public GroupFullDto getGroup(@PathVariable Long id) {
        return groupService.getGroupUsers(id);
    }

    @AdminOrTeacherAccess
    @PostMapping("/search")
    public GroupResponse searchGroups(@RequestBody SearchRequestDto groupRequest) {
        return groupService.getGroups(groupRequest);
    }

    @AdminOrTeacherAccess
    @GetMapping("/search")
    public Page<GroupDto> searchGroups(@PageableDefault(size = 100) Pageable pageable) {
        return groupService.getAllGroups(pageable);
    }

    @AdminOnlyAccess
    @PutMapping
    public GroupDto editGroup(@RequestBody GroupEditionRequestDto request) {
        return groupService.update(request);
    }

    @AdminOnlyAccess
    @DeleteMapping("/{id}")
    public void deleteGroup(@PathVariable Long id) {
        groupService.delete(id);
    }
}
