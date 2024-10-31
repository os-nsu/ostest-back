package ru.nsu.ostest.adapter.in.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.nsu.ostest.adapter.in.rest.model.session.*;
import ru.nsu.ostest.domain.service.SessionService;

import java.util.List;

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

    @GetMapping
    public SessionDto getLabSessionForStudent(@RequestBody GetLabSessionFroStudentRequestDto request) {
        return sessionService.getLabSessionForStudent(request);
    }

    @GetMapping("/user/{userId}")
    public List<SessionDto> getUserSessions(@PathVariable Long userId) {
        return sessionService.getUserSessions(userId);
    }

    @PostMapping("/{sessionId}/attempt")
    public AttemptDto makeAttempt(@RequestBody MakeAttemptDto makeAttemptDto, @PathVariable Long sessionId) {
        return sessionService.makeAttempt(sessionId);
    }

    @GetMapping("/attempt/{attemptId}")
    public AttemptDto getAttempt(@PathVariable Long attemptId) {
        return sessionService.findAttemptById(attemptId);
    }
}
