package ru.nsu.ostest.adapter.in.rest.model.session;

public record MakeAttemptDto(String repositoryUrl,
                             String branch,
                             Long laboratoryId) {
}
