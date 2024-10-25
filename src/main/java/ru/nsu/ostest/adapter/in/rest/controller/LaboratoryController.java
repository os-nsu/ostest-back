package ru.nsu.ostest.adapter.in.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.*;
import ru.nsu.ostest.domain.service.LaboratoryService;

import java.util.List;

@RestController
@RequestMapping("/api/laboratory")
@RequiredArgsConstructor
public class LaboratoryController {

    private final LaboratoryService laboratoryService;

    @GetMapping("/{id}")
    public LaboratoryDto getLaboratory(@PathVariable Long id) {
        return laboratoryService.findById(id);
    }

    @PostMapping("/search")
    public List<LaboratoryShortDto> searchLaboratories(@RequestBody LaboratorySearchRequestDto request) {
        return laboratoryService.searchLaboratories(request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LaboratoryDto createLaboratory(@Valid @RequestBody LaboratoryCreationRequestDto request) {
        return laboratoryService.create(request);
    }

    @PutMapping
    public LaboratoryDto editLaboratory(@RequestBody LaboratoryEditionRequestDto request) {
        return laboratoryService.editLaboratory(request);
    }

    @DeleteMapping("/{id}")
    public void deleteLaboratory(@PathVariable Long id) {
        laboratoryService.deleteById(id);
    }
}
