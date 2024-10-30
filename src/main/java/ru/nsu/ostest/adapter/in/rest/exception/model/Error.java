package ru.nsu.ostest.adapter.in.rest.exception.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Error DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Error {
    @NotNull
    private String message;
}
