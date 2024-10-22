package ru.nsu.ostest.domain.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.ostest.adapter.in.rest.model.laboratory.*;
import ru.nsu.ostest.adapter.in.rest.model.test.TestLaboratoryLinkDto;
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
import java.util.Objects;

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

        List<TestLaboratoryLinkDto> testLinksDtos = laboratoryCreationRequestDto.testsLinks();
        laboratory = laboratoryRepository.save(laboratory);
        for (TestLaboratoryLinkDto testLink : testLinksDtos) {
            Test test = testRepository.findById(testLink.testId()).orElseThrow(() ->
                    new EntityNotFoundException("Test with id: " + testLink.testId() + " not found"));
            laboratory.addTest(test, testLink.isSwitchedOn());
        }

        laboratory = laboratoryRepository.save(laboratory);
        return laboratoryMapper.laboratoryToLaboratoryDto(laboratory);
    }

    @Transactional
    public LaboratoryDto editLaboratory(LaboratoryEditionRequestDto laboratoryEditionRequestDto) {
        checkIfDuplicatedName(laboratoryEditionRequestDto.name(), laboratoryEditionRequestDto.id());

        Laboratory laboratory = laboratoryRepository.findById(laboratoryEditionRequestDto.id())
                .orElseThrow(() -> new EntityNotFoundException("Laboratory not found"));

        laboratoryMapper.updateLaboratoryFromEditionRequestDto(laboratoryEditionRequestDto, laboratory);

        List<TestLaboratoryLinkDto> deleteTestLaboratoryLinks = laboratoryEditionRequestDto.deleteTestsLinks();
        if (CollectionUtils.isNotEmpty(deleteTestLaboratoryLinks)) {
            deleteTestsLinksFromLaboratory(laboratory, deleteTestLaboratoryLinks);
        }

        List<TestLaboratoryLinkDto> editTestLaboratoryLinks = laboratoryEditionRequestDto.editTestsLinks();
        if (CollectionUtils.isNotEmpty(editTestLaboratoryLinks)) {
            editTestsLinksForLaboratory(laboratory, editTestLaboratoryLinks);
        }

        List<TestLaboratoryLinkDto> addTestLaboratoryLinks = laboratoryEditionRequestDto.addTestsLinks();
        if (CollectionUtils.isNotEmpty(addTestLaboratoryLinks)) {
            addTestsLinksToLaboratory(laboratory, addTestLaboratoryLinks);
        }

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

    private void addTestsLinksToLaboratory(Laboratory laboratory,
                                           List<TestLaboratoryLinkDto> addTestLaboratoryLinks) {
        List<TestLaboratoryLink> testLaboratoryLinks = laboratory.getTestsLinks();
        List<Test> labTests = testLaboratoryLinks.stream()
                .map(TestLaboratoryLink::getTest)
                .toList();
        List<TestLaboratoryLinkDto> laboratoryLinksToAdd = addTestLaboratoryLinks.stream()
                .filter(link -> labTests.stream()
                        .noneMatch(test -> Objects.equals(test.getId(), link.testId()))).toList();
        for (TestLaboratoryLinkDto testLaboratoryLinkDto : laboratoryLinksToAdd) {
            Test test = testRepository.findById(testLaboratoryLinkDto.testId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Test with id: " + testLaboratoryLinkDto.testId() + " not found"));
            laboratory.addTest(test, testLaboratoryLinkDto.isSwitchedOn());
        }
        laboratoryRepository.flush();
    }

    private void editTestsLinksForLaboratory(Laboratory laboratory,
                                             List<TestLaboratoryLinkDto> addTestLaboratoryLinks) {
        for (TestLaboratoryLinkDto dto : addTestLaboratoryLinks) {
            TestLaboratoryLink testLaboratoryLink =
                    testLaboratoryLinkRepository.findByLaboratoryIdAndTestId(laboratory.getId(), dto.testId());
            if (testLaboratoryLink != null) {
                testLaboratoryLink.setIsSwitchedOn(dto.isSwitchedOn());
            }
        }
        testLaboratoryLinkRepository.flush();
    }

    private void deleteTestsLinksFromLaboratory(Laboratory laboratory, List<TestLaboratoryLinkDto> deleteTestLaboratoryLinks) {
        List<TestLaboratoryLink> testLaboratoryLinks = laboratory.getTestsLinks();
        List<TestLaboratoryLink> linksToDelete = testLaboratoryLinks.stream()
                .filter(link -> deleteTestLaboratoryLinks.stream()
                        .anyMatch(dto -> Objects.equals(dto.testId(), link.getTest().getId())))
                .toList();
        testLaboratoryLinks.removeAll(linksToDelete);
        laboratoryRepository.flush();
        testLaboratoryLinkRepository.deleteAll(linksToDelete);
    }

    private void checkIfDuplicatedName(String name, Long exceptedId) {
        Laboratory laboratory = laboratoryRepository.findByName(name);
        if (laboratory != null && !laboratory.getId().equals(exceptedId)) {
            throw new DuplicateKeyException(String.format("Laboratory with name '%s' already exists", name));
        }
    }

    private void checkIfDuplicatedName(String name) {
        if (laboratoryRepository.findByName(name) != null) {
            throw new DuplicateKeyException(String.format("Laboratory with name '%s' already exists", name));
        }
    }
}
