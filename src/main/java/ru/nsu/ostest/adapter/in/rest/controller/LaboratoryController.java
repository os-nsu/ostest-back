package ru.nsu.ostest.adapter.in.rest.controller;

import org.springframework.web.bind.annotation.*;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.*;

import java.util.List;

@RestController
@RequestMapping("/api/laboratory")
public class LaboratoryController {

    @GetMapping("/{id}")
    public LaboratoryDto getLaboratory(@PathVariable Long id) {
        throw new IllegalArgumentException("Not implemented");
    }

    @PostMapping("/search")
    public List<LaboratoryShortDto> searchLaboratories(@RequestBody LaboratorySearchRequestDto request) {
        throw new IllegalArgumentException("Not implemented");
    }

    @PostMapping
    public LaboratoryDto createLaboratory(@RequestBody LaboratoryCreationRequestDto request) {
        throw new IllegalArgumentException("Not implemented");
    }

    @PutMapping
    public LaboratoryDto editLaboratory(@RequestBody LaboratoryEditionRequestDto request) {
        throw new IllegalArgumentException("Not implemented");
    }

    @DeleteMapping("/{id}")
    public void deleteLaboratory(@PathVariable Long id) {
        throw new IllegalArgumentException("Not implemented");
    }

}
