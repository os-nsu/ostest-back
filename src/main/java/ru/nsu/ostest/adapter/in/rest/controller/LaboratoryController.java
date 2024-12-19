package ru.nsu.ostest.adapter.in.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.*;
import ru.nsu.ostest.domain.service.LaboratoryService;
import ru.nsu.ostest.security.annotations.AdminOnlyAccess;
import ru.nsu.ostest.security.annotations.AdminOrTeacherAccess;

import java.util.List;

@RestController
@RequestMapping("/api/laboratory")
@RequiredArgsConstructor
public class LaboratoryController {

    private final LaboratoryService laboratoryService;

    @AdminOrTeacherAccess
    @GetMapping("/{id}")
    public LaboratoryDto getLaboratory(@PathVariable Long id) {
        return laboratoryService.findById(id);
    }

    @PostMapping("/search")
    public List<LaboratoryShortDto> searchLaboratories(@RequestBody LaboratorySearchRequestDto request) {
        return laboratoryService.searchLaboratories(request);
    }

    @AdminOnlyAccess
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LaboratoryDto createLaboratory(@Valid @RequestBody LaboratoryCreationRequestDto request) {
        return laboratoryService.create(request);
    }

    @AdminOnlyAccess
    @PutMapping
    public LaboratoryDto editLaboratory(@RequestBody LaboratoryEditionRequestDto request) {
        return laboratoryService.editLaboratory(request);
    }

    @AdminOnlyAccess
    @DeleteMapping("/{id}")
    public void deleteLaboratory(@PathVariable Long id) {
        laboratoryService.deleteById(id);
    }
}
