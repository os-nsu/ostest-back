package ru.nsu.ostestbackend.adapter.in.rest.controller;

import org.springframework.web.bind.annotation.*;
import ru.nsu.ostestbackend.adapter.in.rest.model.test.TestCreationRequestDto;
import ru.nsu.ostestbackend.adapter.in.rest.model.test.TestDto;
import ru.nsu.ostestbackend.adapter.in.rest.model.test.TestEditionRequestDto;
import ru.nsu.ostestbackend.adapter.in.rest.model.test.TestSearchRequestDto;

import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/{id}")
    public TestDto getTest(@PathVariable Long id) {
        throw new IllegalArgumentException("Not implemented");
    }

    @PostMapping("/search")
    public List<TestDto> searchTests(@RequestBody TestSearchRequestDto request) {
        throw new IllegalArgumentException("Not implemented");
    }

    @PostMapping
    public TestDto createTest(@RequestBody TestCreationRequestDto request) {
        throw new IllegalArgumentException("Not implemented");
    }

    @PutMapping
    public TestDto editTest(@RequestBody TestEditionRequestDto request) {
        throw new IllegalArgumentException("Not implemented");
    }

    @DeleteMapping("/{id}")
    public void deleteTest(@PathVariable Long id) {
        throw new IllegalArgumentException("Not implemented");
    }

}
