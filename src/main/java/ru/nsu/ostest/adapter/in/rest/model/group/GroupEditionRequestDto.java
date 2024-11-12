
package ru.nsu.ostest.adapter.in.rest.model.group;

import java.util.Set;

public record GroupEditionRequestDto(
        Long id,
        String name,
        Boolean isArchived,
        Set<Long> addUsers,
        Set<Long> deleteUsers
) {
}
