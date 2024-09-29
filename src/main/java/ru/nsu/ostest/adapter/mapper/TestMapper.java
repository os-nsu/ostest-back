package ru.nsu.ostest.adapter.mapper;

import org.mapstruct.Mapper;
import ru.nsu.ostest.adapter.in.rest.model.test.TestCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestDto;
import ru.nsu.ostest.adapter.out.persistence.entity.test.Test;

@Mapper(componentModel = "spring")
public interface TestMapper {
    TestDto toTestDto(TestCreationRequestDto requestDto);

    Test toTest(TestDto testDto);

    TestDto toTestDtoFromEntity(Test test);
}