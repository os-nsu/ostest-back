
package ru.nsu.ostest.adapter.in.rest.model.group;

import ru.nsu.ostest.adapter.out.persistence.entity.user.User;

import java.util.List;

public record GroupEditionRequestDto(
        Long id,
        String name,
        List<User> users
) {
}
