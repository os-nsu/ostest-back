package ru.nsu.ostest.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryDto;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratorySearchRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryShortDto;
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

    public LaboratoryDto create(LaboratoryCreationRequestDto laboratoryCreationRequestDto) {
        Laboratory laboratory = laboratoryMapper.laboratoryCreationRequestDtoToLaboratory(laboratoryCreationRequestDto);
        if (laboratoryRepository.findByName(laboratoryCreationRequestDto.name()) != null) {
            throw DuplicateLaboratoryNameException.of(laboratory.getName());
        }
        laboratoryRepository.save(laboratory);
        return laboratoryMapper.laboratoryToLaboratoryDto(laboratory);
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
}
