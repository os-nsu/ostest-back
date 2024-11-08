package ru.nsu.ostest.adapter.in.rest.model.session;

import com.fasterxml.jackson.annotation.JsonInclude;
import ru.nsu.ostest.domain.common.enums.AvailabilityStatus;

import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AvailableTaskResponse(UUID id,
                                    String repositoryUrl,
                                    String branch,
                                    Integer laboratoryNumber,
                                    List<String> connectedTests,
                                    AvailabilityStatus status) {

    public static AvailableTaskResponse unavailableTaskResponse() {
        return new AvailableTaskResponse(null, null, null, null,
                        null, AvailabilityStatus.UNAVAILABLE);
    }
}