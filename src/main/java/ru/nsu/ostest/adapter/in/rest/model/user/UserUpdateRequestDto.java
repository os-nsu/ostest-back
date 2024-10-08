package ru.nsu.ostest.adapter.in.rest.model.user;

import lombok.Builder;
import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;

@Data
@Builder
public class UserUpdateRequestDto {
    private JsonNullable<String> username;

    private JsonNullable<String> firstName;

    private JsonNullable<String> secondName;

    private JsonNullable<String> groupNumber;

}
