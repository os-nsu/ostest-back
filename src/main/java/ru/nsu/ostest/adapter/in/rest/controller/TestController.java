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
    public ResponseEntity<String> createTest(
            @RequestPart("data") TestCreationRequestDto request,
            @RequestPart("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("На вход подан пустой файл.");
        }
        byte[] script;
        try {
            script = file.getBytes();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Не удалось прочитать файл.");
        }

        TestDto testDto = testMapper.toTestDto(request);
        Long id = testService.create(testDto, script);
        return ResponseEntity.ok(id.toString());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestDto> getTest(@PathVariable Long id) {
        return ResponseEntity.ok(testService.getTest(id));
    }

    @PostMapping("/search")
    public ResponseEntity<List<ShortTestDto>> searchTests() {
        return ResponseEntity.ok(testService.getAllTests());
    }

    @PutMapping
    public ResponseEntity<String> editTest(
            @RequestPart("data") TestCreationRequestDto request,
            @RequestPart("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("На вход подан пустой файл.");
        }
        byte[] script;
        try {
            script = file.getBytes();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Не удалось прочитать файл.");
        }

        TestDto testDto = testMapper.toTestDto(request);
        Long id = testService.update(testDto, script);
        return ResponseEntity.ok(id.toString());
    }

    @DeleteMapping("/{id}")
    public void deleteTest(@PathVariable Long id) {
        testService.delete(id);
    }

}
