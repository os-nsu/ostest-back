package ru.nsu.ostest.adapter.in.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.*;

import ru.nsu.ostest.adapter.in.rest.model.test.*;
import ru.nsu.ostest.domain.service.TestService;
import org.springframework.web.multipart.MultipartFile;
import ru.nsu.ostest.security.annotations.AdminOnlyAccess;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {
    private final TestService testService;

    @AdminOnlyAccess
    @PostMapping
    public TestDto createTest(
            @RequestPart("data") TestCreationRequestDto request,
            @RequestPart("file") MultipartFile file) {

        return testService.create(request, file);
    }

    @AdminOnlyAccess
    @GetMapping("/{id}")
    public TestDto getTest(@PathVariable Long id) {
        return testService.getTest(id);
    }

    @AdminOnlyAccess
    @GetMapping("/{id}/script")
    public ByteArrayResource getScript(@PathVariable Long id) {
        return testService.getScript(id);
    }

    @AdminOnlyAccess
    @GetMapping("/search")
    public List<ShortTestDto> searchTests() {
        return testService.getAllTests();
    }

    @AdminOnlyAccess
    @PutMapping
    public TestDto editTest(
            @RequestPart("data") TestEditionRequestDto request,
            @RequestPart("file") MultipartFile file) {

        return testService.update(request, file);
    }

    @AdminOnlyAccess
    @DeleteMapping("/{id}")
    public void deleteTest(@PathVariable Long id) {
        testService.delete(id);
    }

}
