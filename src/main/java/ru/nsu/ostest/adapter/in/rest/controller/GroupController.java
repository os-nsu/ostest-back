package ru.nsu.ostest.adapter.in.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.nsu.ostest.adapter.in.rest.model.filter.SearchRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupDto;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupEditionRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupResponse;
import ru.nsu.ostest.domain.service.GroupService;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/group")
public class GroupController {
    private final GroupService groupService;

    @PostMapping
    public GroupDto createGroup(@RequestBody GroupCreationRequestDto request) {
        return groupService.create(request);
    }

    @GetMapping("/{id}")
    public GroupDto getGroup(@PathVariable Long id) {
        return groupService.getGroup(id);
    }

    @PostMapping("/search")
    public GroupResponse searchGroups(@RequestBody SearchRequestDto groupRequest) {
        return groupService.getGroups(groupRequest);
    }

    @GetMapping("/search")
    public Page<GroupDto> searchGroups(@PageableDefault(size = 100) Pageable pageable) {
        return groupService.getAllGroups(pageable);
    }

    @PutMapping
    public GroupDto editGroup(@RequestBody GroupEditionRequestDto request) {
        return groupService.update(request);
    }

    @DeleteMapping("/{id}")
    public void deleteGroup(@PathVariable Long id) {
        groupService.delete(id);
    }
}
