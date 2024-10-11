package ru.nsu.ostest.adapter.in.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.nsu.ostest.adapter.in.rest.model.test.ShortTestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestEditionRequestDto;
import ru.nsu.ostest.domain.service.TestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {
    private final TestService testService;

    @PostMapping
    public TestDto createTest(
            @RequestPart("data") TestCreationRequestDto request,
            @RequestPart("file") MultipartFile file) {

        return testService.create(request, file);
    }

    @GetMapping("/{id}")
    public TestDto getTest(@PathVariable Long id) {
        return testService.getTest(id);
    }

    @GetMapping("/search")
    public List<ShortTestDto> searchTests() {
        return testService.getAllTests();
    }

    @PutMapping
    public TestDto editTest(
            @RequestPart("data") TestEditionRequestDto request,
            @RequestPart("file") MultipartFile file) {

        return testService.update(request, file);
    }

    @DeleteMapping("/{id}")
    public void deleteTest(@PathVariable Long id) {
        testService.delete(id);
    }

}
