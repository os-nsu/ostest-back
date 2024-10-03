package ru.nsu.ostest.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.nsu.ostest.adapter.in.rest.model.test.ShortTestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestDto;
import ru.nsu.ostest.adapter.out.persistence.entity.test.Test;
import ru.nsu.ostest.adapter.out.persistence.entity.test.TestLaboratoryLink;

@Mapper(componentModel = "spring")
public interface TestMapper {
    ShortTestDto toShortTestDtoFromEntity(Test test);

    TestDto toTestDto(TestCreationRequestDto requestDto);

    Test toTest(TestDto testDto);

    TestDto toTestDtoFromEntity(Test test);

    @Mapping(source = "testLaboratoryLink.test.id", target = "id")
    @Mapping(source = "testLaboratoryLink.test.name", target = "name")
    @Mapping(source = "testLaboratoryLink.test.description", target = "description")
    @Mapping(source = "testLaboratoryLink.test.category", target = "testCategory")
    TestDto fromTestLaboratoryLink(TestLaboratoryLink testLaboratoryLink);
}