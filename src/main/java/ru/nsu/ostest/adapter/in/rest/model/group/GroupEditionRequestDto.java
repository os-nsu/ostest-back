
package ru.nsu.ostest.adapter.in.rest.model.group;

import java.util.List;

public record GroupEditionRequestDto(
        Long id,
        String name,
        List<Long> usersIds
) {
}
