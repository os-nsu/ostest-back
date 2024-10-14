package ru.nsu.ostest.adapter.in.rest.model.session;

import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryShortDto;
import ru.nsu.ostest.adapter.in.rest.model.user.UserDto;

import java.util.List;

public record SessionDto(
        Long id,
        UserDto student,
        UserDto teacher,
        LaboratoryShortDto laboratory,
        List<AttemptDto> attempts
) {
}
