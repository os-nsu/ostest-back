package ru.nsu.ostest.domain.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.nsu.ostest.adapter.in.rest.model.test.ShortTestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestEditionRequestDto;
import ru.nsu.ostest.adapter.mapper.TestMapper;
import ru.nsu.ostest.adapter.out.persistence.entity.test.Test;
import ru.nsu.ostest.domain.exception.DuplicateTestNameException;
import ru.nsu.ostest.domain.repository.TestRepository;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TestService {
    private static final String TEST_CREATED_MESSAGE_TEMPLATE = "Entity test created: {}";
    private static final String TEST_SAVED_MESSAGE_TEMPLATE = "Entity test saved: {}";
    private static final String TEST_FOUND_MESSAGE_TEMPLATE = "Found test with id = {}";
    private static final String TESTS_FOUND_MESSAGE_TEMPLATE = "N = {} tests found.";
    private static final String TEST_DELETED_MESSAGE_TEMPLATE = "Test with id = {} deleted";
    private static final String TEST_NOT_FOUND_MESSAGE_TEMPLATE = "Test not found.";
    private static final String TEST_REPLACED_MESSAGE_TEMPLATE = "Test named {} replaced by test {}}";
    private static final String FILE_READ_MESSAGE = "File read.";
    private static final String DUPLICATED_NAME_MESSAGE = "A file with this name already exists.";
    private static final String FILE_READING_FAILED_MESSAGE = "Failed to read file.";

    private final TestRepository testRepository;
    private final TestMapper testMapper;


    @Transactional
    public TestDto create(TestCreationRequestDto testCreationRequestDto, MultipartFile file) {
        checkIfDuplicatedName(testCreationRequestDto.name());
        Test test = testMapper.testCreationRequestDtoToTest(testCreationRequestDto);
        test.setScriptBody(getBytesFromFile(file));
        log.debug(TEST_CREATED_MESSAGE_TEMPLATE, test);

        test = testRepository.save(test);
        log.info(TEST_SAVED_MESSAGE_TEMPLATE, test);
        return testMapper.testToTestDto(test);
    }

    public TestDto getTest(Long id) {
        TestDto testDto = testMapper.testToTestDto(testRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TEST_NOT_FOUND_MESSAGE_TEMPLATE)));

        log.info(TEST_FOUND_MESSAGE_TEMPLATE, id);
        return testDto;
    }

    public List<ShortTestDto> getAllTests() {
        List<ShortTestDto> list = testMapper.testsToShortTestDtoList(testRepository.findAll());

        log.info(TESTS_FOUND_MESSAGE_TEMPLATE, list.size());
        return list;
    }

    @Transactional
    public void delete(Long id) {
        testRepository.deleteById(id);
        log.info(TEST_DELETED_MESSAGE_TEMPLATE, id);
    }

    @Transactional
    public TestDto update(TestEditionRequestDto testEditionRequestDto, MultipartFile file) {
        checkIfDuplicatedName(testEditionRequestDto.name(), testEditionRequestDto.id());

        Test test = testRepository.findById(testEditionRequestDto.id())
                .orElseThrow(() -> new EntityNotFoundException(TEST_NOT_FOUND_MESSAGE_TEMPLATE));
        testMapper.testEditionRequestDtoToTest(test, testEditionRequestDto, getBytesFromFile(file));

        log.info(TEST_REPLACED_MESSAGE_TEMPLATE, testEditionRequestDto.name(), test);
        return testMapper.testToTestDto(test);
    }

    private static byte[] getBytesFromFile(MultipartFile file) {
        byte[] script;
        try {
            script = file.getBytes();
        } catch (IOException e) {
            log.error(FILE_READING_FAILED_MESSAGE);
            throw new RuntimeException(FILE_READING_FAILED_MESSAGE);
        }
        log.info(FILE_READ_MESSAGE);
        return script;
    }

    private void checkIfDuplicatedName(String name, Long exceptedId) {
        Test test = testRepository.findByName(name);
        if (test != null && !test.getId().equals(exceptedId)) {
            log.error(DUPLICATED_NAME_MESSAGE);
            throw DuplicateTestNameException.of(name);
        }
    }

    private void checkIfDuplicatedName(String name) {
        if (testRepository.findByName(name) != null) {
            log.error(DUPLICATED_NAME_MESSAGE);
            throw DuplicateTestNameException.of(name);
        }
    }
}
