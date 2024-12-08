package ru.nsu.ostest.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.nsu.ostest.adapter.in.rest.model.test.*;
import ru.nsu.ostest.adapter.out.persistence.entity.test.Test;
import ru.nsu.ostest.adapter.out.persistence.entity.test.TestLaboratoryLink;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface TestMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "scriptBody", ignore = true)
    @Mapping(target = "laboratoriesLinks", ignore = true)
    Test testCreationRequestDtoToTest(TestCreationRequestDto testCreationRequestDto);

    @Mapping(target = "laboratoriesLinks", ignore = true)
    @Mapping(source = "script", target = "test.scriptBody")
    void testEditionRequestDtoToTest(
            @MappingTarget Test test, TestEditionRequestDto testEditionRequestDto, byte[] script
    );

    TestDto testToTestDto(Test test);

    List<ShortTestDto> testsToShortTestDtoList(List<Test> tests);

    @Mapping(source = "testLaboratoryLink.test.id", target = "id")
    @Mapping(source = "testLaboratoryLink.test.name", target = "name")
    @Mapping(source = "testLaboratoryLink.test.description", target = "description")
    @Mapping(source = "testLaboratoryLink.test.code", target = "code")
    @Mapping(source = "testLaboratoryLink.test.category", target = "category")
    LaboratoryTestDto fromTestLaboratoryLink(TestLaboratoryLink testLaboratoryLink);
}