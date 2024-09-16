package ru.nsu.ostestbackend.adapter.in.rest.controller;

import org.springframework.web.bind.annotation.*;
import ru.nsu.ostestbackend.adapter.in.rest.model.group.GroupCreationRequestDto;
import ru.nsu.ostestbackend.adapter.in.rest.model.group.GroupDto;
import ru.nsu.ostestbackend.adapter.in.rest.model.group.GroupEditionRequestDto;
import ru.nsu.ostestbackend.adapter.in.rest.model.group.GroupSearchRequestDto;

import java.util.List;

@RestController
@RequestMapping("/api/group")
public class GroupController {

    @GetMapping("/{id}")
    public GroupDto getGroup(@PathVariable Long id) {
        throw new IllegalArgumentException("Not implemented");
    }

    @PostMapping("/search")
    public List<GroupDto> searchGroups(@RequestBody GroupSearchRequestDto request) {
        throw new IllegalArgumentException("Not implemented");
    }

    @PostMapping
    public GroupDto createGroup(@RequestBody GroupCreationRequestDto request) {
        throw new IllegalArgumentException("Not implemented");
    }

    @PutMapping
    public GroupDto editGroup(@RequestBody GroupEditionRequestDto request) {
        throw new IllegalArgumentException("Not implemented");
    }

    @DeleteMapping("/{id}")
    public void deleteGroup(@PathVariable Long id) {
        throw new IllegalArgumentException("Not implemented");
    }

}
