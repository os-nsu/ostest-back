package ru.nsu.ostest.domain.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.*;
import ru.nsu.ostest.adapter.mapper.LaboratoryMapper;
import ru.nsu.ostest.adapter.out.persistence.entity.laboratory.Laboratory;
import ru.nsu.ostest.domain.repository.LaboratoryRepository;
import ru.nsu.ostest.domain.exception.DuplicateLaboratoryNameException;
import ru.nsu.ostest.domain.specification.LaboratorySpecification;

import java.util.List;


@Service
@RequiredArgsConstructor
public class LaboratoryService {

    private final LaboratoryRepository laboratoryRepository;
    private final LaboratoryMapper laboratoryMapper;

    @Transactional
    public void deleteById(Long id) {
        laboratoryRepository.deleteById(id);
    }

    @Transactional
    public LaboratoryDto create(LaboratoryCreationRequestDto laboratoryCreationRequestDto) {
        Laboratory laboratory = laboratoryMapper.laboratoryCreationRequestDtoToLaboratory(laboratoryCreationRequestDto);
        checkIfDuplicatedName(laboratoryCreationRequestDto.name());
        laboratoryRepository.save(laboratory);
        return laboratoryMapper.laboratoryToLaboratoryDto(laboratory);
    }

    public LaboratoryDto editLaboratory(LaboratoryEditionRequestDto laboratoryEditionRequestDto) {
        checkIfDuplicatedName(laboratoryEditionRequestDto.name(), laboratoryEditionRequestDto.id());

        Laboratory laboratory = laboratoryRepository.findById(laboratoryEditionRequestDto.id())
                .orElseThrow(() -> new EntityNotFoundException("Laboratory not found"));

        laboratory.setName(laboratoryEditionRequestDto.name());
        laboratory.setDescription(laboratoryEditionRequestDto.description());
        laboratory.setSemesterNumber(laboratoryEditionRequestDto.semesterNumber());
        laboratory.setDeadline(laboratoryEditionRequestDto.deadline());
        laboratory.setIsHidden(laboratoryEditionRequestDto.isHidden());

        Laboratory updatedLaboratory = laboratoryRepository.save(laboratory);

        return laboratoryMapper.laboratoryToLaboratoryDto(updatedLaboratory);
    }

    public List<LaboratoryShortDto> searchLaboratories(LaboratorySearchRequestDto laboratorySearchRequestDto) {
        Boolean isHidden = laboratorySearchRequestDto.isHidden();
        Integer semesterNumber = laboratorySearchRequestDto.semesterNumber();
        Specification<Laboratory> spec = LaboratorySpecification.byIsHiddenAndSemesterNumber(isHidden, semesterNumber);
        return laboratoryMapper.laboratoriesToLaboratoryShortDtoList(laboratoryRepository.findAll(spec));
    }

    public LaboratoryDto findById(Long id) {
        return laboratoryMapper.laboratoryToLaboratoryDto(laboratoryRepository.findById(id).orElse(null));
    }

    private void checkIfDuplicatedName(String name, Long exceptedId) {
        Laboratory laboratory = laboratoryRepository.findByName(name);
        if (laboratory != null && !laboratory.getId().equals(exceptedId)) {
            throw DuplicateLaboratoryNameException.of(name);
        }
    }

    private void checkIfDuplicatedName(String name) {
        if (laboratoryRepository.findByName(name) != null) {
            throw DuplicateLaboratoryNameException.of(name);
        }
    }
}
