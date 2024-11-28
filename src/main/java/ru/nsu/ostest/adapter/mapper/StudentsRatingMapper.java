package ru.nsu.ostest.adapter.mapper;

import org.mapstruct.Mapper;
import ru.nsu.ostest.adapter.in.rest.model.user.userData.StudentRatingDTO;
import ru.nsu.ostest.adapter.out.persistence.entity.user.StudentRating;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface StudentsRatingMapper {
    StudentRatingDTO mapToStudentRatingDTO(StudentRating studentRating);
}
