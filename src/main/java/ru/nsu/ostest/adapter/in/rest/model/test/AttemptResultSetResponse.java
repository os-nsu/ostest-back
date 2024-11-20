package ru.nsu.ostest.adapter.in.rest.model.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttemptResultSetResponse {
    private UUID id;
}
