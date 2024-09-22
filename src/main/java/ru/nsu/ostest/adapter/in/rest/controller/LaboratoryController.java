package ru.nsu.ostest.adapter.in.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.*;
import ru.nsu.ostest.domain.exception.DuplicateLaboratoryNameException;
import ru.nsu.ostest.domain.service.LaboratoryService;

import java.util.List;

@RestController
@RequestMapping("/api/laboratory")
@RequiredArgsConstructor
public class LaboratoryController {

    private final LaboratoryService laboratoryService;

    @GetMapping("/{id}")
    public LaboratoryDto getLaboratory(@PathVariable Long id) {
        throw new IllegalArgumentException("Not implemented");
    }

    @PostMapping("/search")
    public List<LaboratoryShortDto> searchLaboratories(@RequestBody LaboratorySearchRequestDto request) {
        throw new IllegalArgumentException("Not implemented");
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LaboratoryDto createLaboratory(@RequestBody LaboratoryCreationRequestDto request)
            throws DuplicateLaboratoryNameException {
        return laboratoryService.create(request);
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
