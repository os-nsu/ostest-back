package ru.nsu.ostest.domain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import ru.nsu.ostest.adapter.in.rest.model.test.*;
import ru.nsu.ostest.adapter.out.persistence.entity.test.Test;
import ru.nsu.ostest.adapter.mapper.TestMapper;
import ru.nsu.ostest.domain.exception.validation.DuplicateTestNameException;
import ru.nsu.ostest.domain.exception.validation.TestNotFoundException;
import ru.nsu.ostest.domain.repository.TestRepository;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TestService {
    private static final String FILE_READING_FAILED_MESSAGE = "Failed to read file.";

    private final TestRepository testRepository;
    private final TestMapper testMapper;


    @Transactional
    public TestDto create(TestCreationRequestDto testCreationRequestDto, MultipartFile file) {
        checkIfDuplicatedName(testCreationRequestDto.name());
        Test test = testMapper.testCreationRequestDtoToTest(testCreationRequestDto);
        test.setScriptBody(getBytesFromFile(file));

        test = testRepository.save(test);
        log.info("Entity test saved: {}", test);
        return testMapper.testToTestDto(test);
    }

    public TestDto getTest(Long id) {
        return testMapper.testToTestDto(testRepository.findById(id)
                .orElseThrow(() -> TestNotFoundException.notFoundTestWithId(id)));
    }

    public ByteArrayResource getScript(Long id) {
        Test test = testRepository.findById(id)
                .orElseThrow(() -> TestNotFoundException.notFoundTestWithId(id));

        return new ByteArrayResource(test.getScriptBody());
    }

    public List<ShortTestDto> getAllTests() {
        return testMapper.testsToShortTestDtoList(testRepository.findAll());
    }

    @Transactional
    public void delete(Long id) {
        testRepository.deleteById(id);
        log.info("Test with id = {} deleted", id);
    }

    @Transactional
    public TestDto update(TestEditionRequestDto testEditionRequestDto, MultipartFile file) {
        checkIfDuplicatedName(testEditionRequestDto.name(), testEditionRequestDto.id());

        Test test = testRepository.findById(testEditionRequestDto.id())
                .orElseThrow(() -> TestNotFoundException.notFoundTestWithId(testEditionRequestDto.id()));
        testMapper.testEditionRequestDtoToTest(test, testEditionRequestDto, getBytesFromFile(file));

        log.info("Test named {} replaced by test {}}", testEditionRequestDto.name(), test.toString());
        return testMapper.testToTestDto(test);
    }

    private static byte[] getBytesFromFile(MultipartFile file) {
        byte[] script;
        try {
            script = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(FILE_READING_FAILED_MESSAGE);
        }
        log.info("File read.");
        return script;
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
