package ru.nsu.ostest.adapter.in.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.nsu.ostest.adapter.in.rest.model.session.*;
import ru.nsu.ostest.domain.service.SessionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/session")
public class SessionController {

    private final SessionService sessionService;

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
    public SessionDto getLabSessionForStudent(@RequestBody GetLabSessionFromStudentRequestDto request) {
        return sessionService.getLabSessionForStudent(request);
    }

    @GetMapping("/user/{userId}")
    public List<SessionDto> getUserSessions(@PathVariable Long userId) {
        return sessionService.getUserSessions(userId);
    }

    @PostMapping("/{sessionId}/attempt")
    @ResponseStatus(HttpStatus.CREATED)
    public AttemptDto makeAttempt(@RequestBody MakeAttemptDto makeAttemptDto, @PathVariable Long sessionId) {
        return sessionService.makeAttempt(makeAttemptDto, sessionId);
    }

    @GetMapping("/attempt/{attemptId}")
    public AttemptDto getAttempt(@PathVariable UUID attemptId) {
        return sessionService.findAttemptById(attemptId);
    }
}
