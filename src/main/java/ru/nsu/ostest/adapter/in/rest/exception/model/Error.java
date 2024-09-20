package ru.nsu.ostest.adapter.in.rest.exception.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("code")
    @NotNull
    private Integer code;

    @JsonProperty("message")
    @NotNull
    private String message;
}
