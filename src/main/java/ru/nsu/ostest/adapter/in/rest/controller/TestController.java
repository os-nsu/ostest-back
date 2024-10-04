package ru.nsu.ostest.adapter.in.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.nsu.ostest.adapter.in.rest.model.test.*;
import ru.nsu.ostest.adapter.mapper.TestMapper;
import ru.nsu.ostest.domain.service.TestService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {
    private final TestService testService;
    private final TestMapper testMapper;

    @PostMapping
    public TestDto createTest(
            @RequestPart("data") TestCreationRequestDto request,
            @RequestPart("file") MultipartFile file) {

        if (file.isEmpty()) {
            throw new RuntimeException("На вход подан пустой файл.");
        }
        byte[] script;
        try {
            script = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать файл.");
        }

        TestDto testDto = testMapper.toTestDto(request);
        return testService.create(testDto, script);
    }

    @GetMapping("/{id}")
    public TestDto getTest(@PathVariable Long id) {
        return testService.getTest(id);
    }

    @PostMapping("/search")
    public List<ShortTestDto> searchTests() {
        return testService.getAllTests();
    }

    @PutMapping
    public TestDto editTest(
            @RequestPart("data") TestCreationRequestDto request,
            @RequestPart("file") MultipartFile file) {

        if (file.isEmpty()) {
            throw new RuntimeException("На вход подан пустой файл.");
        }
        byte[] script;
        try {
            script = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать файл.");
        }

        TestDto testDto = testMapper.toTestDto(request);
        return testService.update(testDto, script);
    }

    @DeleteMapping("/{id}")
    public void deleteTest(@PathVariable Long id) {
        testService.delete(id);
    }

}
