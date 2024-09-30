package ru.nsu.ostest.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryDto;
import ru.nsu.ostest.adapter.mapper.LaboratoryMapper;
import ru.nsu.ostest.adapter.out.persistence.entity.laboratory.Laboratory;
import ru.nsu.ostest.domain.repository.LaboratoryRepository;
import ru.nsu.ostest.domain.exception.DuplicateLaboratoryNameException;


@Service
@RequiredArgsConstructor
public class LaboratoryService {

    private final LaboratoryRepository laboratoryRepository;
    private final LaboratoryMapper laboratoryMapper;

    public LaboratoryDto create(LaboratoryCreationRequestDto laboratoryCreationRequestDto) {
        Laboratory laboratory = laboratoryMapper.laboratoryCreationRequestDtoToLaboratory(laboratoryCreationRequestDto);
        if (laboratoryRepository.findByName(laboratoryCreationRequestDto.name()) == null) {
            throw DuplicateLaboratoryNameException.of(laboratory.getName());
        }
        laboratoryRepository.save(laboratory);
        return laboratoryMapper.laboratoryToLaboratoryDto(laboratory);
    }
}
