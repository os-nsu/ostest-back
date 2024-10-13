package ru.nsu.ostest.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.nsu.ostest.adapter.in.rest.model.test.ShortTestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestDto;
import ru.nsu.ostest.adapter.in.rest.model.test.TestEditionRequestDto;
import ru.nsu.ostest.adapter.out.persistence.entity.test.Test;
import ru.nsu.ostest.adapter.out.persistence.entity.test.TestLaboratoryLink;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface TestMapper {
    @Mapping(source = "testCreationRequestDto.testCategory", target = "category")
    Test testCreationRequestDtoToTest(TestCreationRequestDto testCreationRequestDto);

    @Mapping(source = "testEditionRequestDto.testCategory", target = "category")
    @Mapping(source = "script", target = "test.scriptBody")
    void testEditionRequestDtoToTest(
            @MappingTarget Test test, TestEditionRequestDto testEditionRequestDto, byte[] script
    );

    @Mapping(source = "category", target = "testCategory")
    TestDto testToTestDto(Test test);

    List<ShortTestDto> testsToShortTestDtoList(List<Test> tests);

    @Mapping(source = "testLaboratoryLink.test.id", target = "id")
    @Mapping(source = "testLaboratoryLink.test.name", target = "name")
    @Mapping(source = "testLaboratoryLink.test.description", target = "description")
    @Mapping(source = "testLaboratoryLink.test.category", target = "testCategory")
    TestDto fromTestLaboratoryLink(TestLaboratoryLink testLaboratoryLink);
}