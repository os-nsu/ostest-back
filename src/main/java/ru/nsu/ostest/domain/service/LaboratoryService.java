package ru.nsu.ostest.domain.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.*;
import ru.nsu.ostest.adapter.in.rest.model.test.TestDto;
import ru.nsu.ostest.adapter.mapper.LaboratoryMapper;
import ru.nsu.ostest.adapter.out.persistence.entity.laboratory.Laboratory;
import ru.nsu.ostest.adapter.out.persistence.entity.test.Test;
import ru.nsu.ostest.adapter.out.persistence.entity.test.TestLaboratoryLink;
import ru.nsu.ostest.domain.repository.LaboratoryRepository;
import ru.nsu.ostest.domain.exception.DuplicateLaboratoryNameException;
import ru.nsu.ostest.domain.repository.TestLaboratoryLinkRepository;
import ru.nsu.ostest.domain.repository.TestRepository;
import ru.nsu.ostest.domain.specification.LaboratorySpecification;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LaboratoryService {

    private final LaboratoryRepository laboratoryRepository;
    private final LaboratoryMapper laboratoryMapper;
    private final TestRepository testRepository;
    private final TestLaboratoryLinkRepository testLaboratoryLinkRepository;

    @Transactional
    public void deleteById(Long id) {
        laboratoryRepository.deleteById(id);
    }

    @Transactional
    public LaboratoryDto create(LaboratoryCreationRequestDto laboratoryCreationRequestDto) {
        Laboratory laboratory = laboratoryMapper.laboratoryCreationRequestDtoToLaboratory(laboratoryCreationRequestDto);
        checkIfDuplicatedName(laboratoryCreationRequestDto.name());

        List<TestDto> testDtos = laboratoryCreationRequestDto.tests();
        laboratory = laboratoryRepository.save(laboratory);
        for (TestDto testDto : testDtos) {
            Test test = testRepository.findById(testDto.id()).orElseThrow(() ->
                    new EntityNotFoundException("Test with name: " + testDto.name() + " not found"));
            TestLaboratoryLink testLaboratoryLink = new TestLaboratoryLink();
            testLaboratoryLink.setTest(test);
            testLaboratoryLink.setLaboratory(laboratory);
            testLaboratoryLink = testLaboratoryLinkRepository.save(testLaboratoryLink);
            laboratory.getTestsLinks().add(testLaboratoryLink);
        }

        laboratory = laboratoryRepository.save(laboratory);
        return laboratoryMapper.laboratoryToLaboratoryDto(laboratory);
    }

    @Transactional
    public LaboratoryDto editLaboratory(LaboratoryEditionRequestDto laboratoryEditionRequestDto) {
        checkIfDuplicatedName(laboratoryEditionRequestDto.name(), laboratoryEditionRequestDto.id());

        Laboratory existingLaboratory = laboratoryRepository.findById(laboratoryEditionRequestDto.id())
                .orElseThrow(() -> new EntityNotFoundException("Laboratory not found"));

        List<Test> tests = laboratoryEditionRequestDto.tests().stream()
                .map((testDto) -> testRepository.findById(testDto.id()).orElseThrow(() ->
                        new EntityNotFoundException("Test with name: " + testDto.name() + " not found")))
                .toList();

        Laboratory updatedLaboratory
                = laboratoryMapper.laboratoryEditionRequestDtoToLaboratory(laboratoryEditionRequestDto);
        updatedLaboratory = laboratoryRepository.save(updatedLaboratory);

        for (Test test : tests) {
            TestLaboratoryLink testLaboratoryLink =
                    testLaboratoryLinkRepository.findByLaboratoryIdAndTestId(existingLaboratory.getId(), test.getId());

            if (testLaboratoryLink == null) {
                testLaboratoryLink = new TestLaboratoryLink();
                testLaboratoryLink.setTest(test);
                testLaboratoryLink.setLaboratory(updatedLaboratory);
                testLaboratoryLink = testLaboratoryLinkRepository.save(testLaboratoryLink);
            }
            updatedLaboratory.getTestsLinks().add(testLaboratoryLink);
        }

        List<TestLaboratoryLink> existingLaboratoryTestLinks = existingLaboratory.getTestsLinks();

        for (TestLaboratoryLink testLaboratoryLink : existingLaboratoryTestLinks) {
            if (!updatedLaboratory.getTestsLinks().contains(testLaboratoryLink)) {
                testLaboratoryLinkRepository.delete(testLaboratoryLink);
            }
        }
        updatedLaboratory = laboratoryRepository.save(updatedLaboratory);

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
