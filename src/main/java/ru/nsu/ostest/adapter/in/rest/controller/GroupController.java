package ru.nsu.ostest.adapter.in.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupDto;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupEditionRequestDto;
import ru.nsu.ostest.domain.service.GroupService;

import java.util.List;


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

    @GetMapping("/search")
    public List<GroupDto> searchGroups() {
        return groupService.getAllGroups();
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
