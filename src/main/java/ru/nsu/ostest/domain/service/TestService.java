package ru.nsu.ostest.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ru.nsu.ostest.adapter.in.rest.model.test.ShortTestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestDto;
import ru.nsu.ostest.adapter.mapper.TestMapper;
import ru.nsu.ostest.adapter.out.persistence.entity.test.Test;
import ru.nsu.ostest.domain.exception.DuplicateTestNameException;
import ru.nsu.ostest.domain.repository.TestRepository;
import ru.nsu.ostest.security.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Service
public class TestService {
    private final TestRepository testRepository;
    private final TestMapper testMapper;

    public TestDto create(TestDto testDto, byte[] script) {
        Test test = testMapper.toTest(testDto);
        test.setScriptBody(script);

        if (testRepository.findByName(test.getName()) != null) {
            throw DuplicateTestNameException.of(test.getName());
        }

        try {
            return testMapper.toTestDtoFromEntity(testRepository.save(test));
        } catch (DuplicateKeyException e) {
            throw new RuntimeException("Запись с таким ключом уже существует." + e.getMessage());
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Ошибка целостности данных." + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении данных." + e.getMessage());
        }
    }

    public TestDto getTest(Long id) {
        Test test = testRepository.findById(id).orElse(null);
        if (test == null) {
            throw new NotFoundException("Test with the given id not found.");
        }
        return testMapper.toTestDtoFromEntity(test);
    }

    public List<ShortTestDto> getAllTests() {
        List<Test> tests = testRepository.findAll();
        List<ShortTestDto> shortTests = new ArrayList<>();

        for (Test t : tests) {
            shortTests.add(testMapper.toShortTestDtoFromEntity(t));
        }
        return shortTests;
    }

    public void delete(Long id) {
        if (testRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Test with the given id not found.");
        }
        try {
            testRepository.deleteById(id);
        } catch (Exception e) {//todo: конкретизировать исключения
            throw new RuntimeException("Не удалось удалить сущность.");
        }
    }

    public TestDto update(TestDto testDto, byte[] script) {
        Test test = testMapper.toTest(testDto);

        delete(test.getId());
        return create(testDto, script);
    }
}
