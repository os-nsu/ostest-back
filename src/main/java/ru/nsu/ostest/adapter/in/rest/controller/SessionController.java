package ru.nsu.ostest.adapter.in.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.nsu.ostest.adapter.in.rest.model.session.*;
import ru.nsu.ostest.domain.service.AttemptService;
import ru.nsu.ostest.domain.service.SessionService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/session")
public class SessionController {

    private final SessionService sessionService;
    private final AttemptService attemptService;

    @PostMapping("/start")
    @ResponseStatus(HttpStatus.CREATED)
    public SessionDto startSession(@RequestBody StartSessionRequestDto request) {
        return sessionService.create(request);
    }

    @GetMapping("/{id}")
    public SessionDto getSession(@PathVariable Long id) {
        return sessionService.findById(id);
    }

    @PostMapping("/lab-student")
    public SessionShortDto getLabSessionForStudent(@RequestBody GetLabSessionFromStudentRequestDto request) {
        return sessionService.getLabSessionForStudent(request);
    }

    @GetMapping("/user/{userId}")
    public Page<SessionShortDto> getUserSessions(@PathVariable Long userId,
                                                 @PageableDefault(size = 100) Pageable pageable) {
        return sessionService.getUserSessions(userId, pageable);
    }

    @PostMapping("/{sessionId}/attempt")
    @ResponseStatus(HttpStatus.CREATED)
    public AttemptDto makeAttempt(@RequestBody MakeAttemptDto makeAttemptDto, @PathVariable Long sessionId) {
        return attemptService.makeAttempt(makeAttemptDto, sessionId);
    }

    @GetMapping("/attempt/{attemptId}")
    public AttemptDto getAttempt(@PathVariable UUID attemptId) {
        return attemptService.getAttemptById(attemptId);
    }
}
