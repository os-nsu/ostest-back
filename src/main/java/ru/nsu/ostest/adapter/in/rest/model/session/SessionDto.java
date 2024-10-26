package ru.nsu.ostest.adapter.in.rest.model.session;

import ru.nsu.ostest.adapter.in.rest.model.laboratory.LaboratoryShortDto;
import ru.nsu.ostest.adapter.in.rest.model.user.userData.UserDto;

import java.util.List;

public record SessionDto(
        Long id,
        UserDto student,
        UserDto teacher,
        LaboratoryShortDto laboratory,
        List<AttemptShortDto> attempts
) {
}
