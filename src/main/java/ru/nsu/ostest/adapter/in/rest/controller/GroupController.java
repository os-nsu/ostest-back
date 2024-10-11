package ru.nsu.ostest.adapter.in.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupDto;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupEditionRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.group.GroupSearchRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestDto;
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


    //----------------

    @GetMapping("/{id}")
    public GroupDto getGroup(@PathVariable Long id) {
        throw new IllegalArgumentException("Not implemented");
    }

    @GetMapping("/search")
    public List<GroupDto> searchGroups(@RequestBody GroupSearchRequestDto request) {
        throw new IllegalArgumentException("Not implemented");
    }

//    @PostMapping
//    public GroupDto createGroup(@RequestBody GroupCreationRequestDto request) {
//
//        throw new IllegalArgumentException("Not implemented");
//    }

    @PutMapping
    public GroupDto editGroup(@RequestBody GroupEditionRequestDto request) {
        throw new IllegalArgumentException("Not implemented");
    }

    @DeleteMapping("/{id}")
    public void deleteGroup(@PathVariable Long id) {
        throw new IllegalArgumentException("Not implemented");
    }

}
