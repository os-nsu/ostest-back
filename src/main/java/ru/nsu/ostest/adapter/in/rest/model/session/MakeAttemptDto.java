package ru.nsu.ostest.adapter.in.rest.model.session;

public record MakeAttemptDto(String repositoryLink,
                             String branchName,
                             Long laboratoryId) {
}
