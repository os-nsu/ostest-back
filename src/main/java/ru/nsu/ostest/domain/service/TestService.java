package ru.nsu.ostest.domain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.nsu.ostest.adapter.in.rest.model.test.*;
import ru.nsu.ostest.adapter.out.persistence.entity.test.Test;
import ru.nsu.ostest.adapter.mapper.TestMapper;
import ru.nsu.ostest.domain.repository.TestRepository;

import org.springframework.web.multipart.MultipartFile;
import ru.nsu.ostest.domain.exception.DuplicateTestNameException;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;

import java.util.List;


@RequiredArgsConstructor
@Service
public class TestService {
    private final TestRepository testRepository;
    private final TestMapper testMapper;


    @Transactional
    public TestDto create(TestCreationRequestDto testCreationRequestDto, MultipartFile file) {
        checkIfDuplicatedName(testCreationRequestDto.name());

        byte[] script;
        try {
            script = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать файл.");
        }

        Test test = testMapper.testCreationRequestDtoToTest(testCreationRequestDto);
        test.setScriptBody(script);

        test = testRepository.save(test);
        return testMapper.testToTestDto(test);
    }

    public TestDto getTest(Long id) {
        return testMapper.testToTestDto(testRepository.findById(id).orElse(null));
    }

    public List<ShortTestDto> getAllTests() {
        return testMapper.testsToShortTestDtoList(testRepository.findAll());
    }

    @Transactional
    public void delete(Long id) {
        testRepository.deleteById(id);
    }

    @Transactional
    public TestDto update(TestEditionRequestDto testEditionRequestDto, MultipartFile file) {
        checkIfDuplicatedName(testEditionRequestDto.name(), testEditionRequestDto.id());

        byte[] script;
        try {
            script = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать файл.");
        }

        Test test = testMapper.testEditionRequestDtoToTest(testEditionRequestDto);
        test.setScriptBody(script);

        testRepository.findById(testEditionRequestDto.id())
                .orElseThrow(() -> new EntityNotFoundException("Test not found"));

        Test updatedTest
                = testMapper.testEditionRequestDtoToTest(testEditionRequestDto);

        updatedTest = testRepository.save(updatedTest);

        return testMapper.testToTestDto(updatedTest);
    }

    private void checkIfDuplicatedName(String name, Long exceptedId) {
        Test test = testRepository.findByName(name);
        if (test != null && !test.getId().equals(exceptedId)) {
            throw DuplicateTestNameException.of(name);
        }
    }

    private void checkIfDuplicatedName(String name) {
        if (testRepository.findByName(name) != null) {
            throw DuplicateTestNameException.of(name);
        }
    }
}
