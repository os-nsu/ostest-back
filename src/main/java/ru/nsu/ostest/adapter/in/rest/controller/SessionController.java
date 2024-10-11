package ru.nsu.ostest.adapter.in.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nsu.ostest.adapter.in.rest.model.session.AttemptDto;
import ru.nsu.ostest.adapter.in.rest.model.session.SearchSessionRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.session.SessionDto;
import ru.nsu.ostest.adapter.in.rest.model.session.StartSessionRequestDto;
import ru.nsu.ostest.domain.service.SessionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/session")
public class SessionController {

    private final SessionService sessionService;

    @PostMapping("/start")
    public SessionDto startSession(@RequestBody StartSessionRequestDto request) {
        return sessionService.create(request);
    }

    @GetMapping("/{id}")
    public SessionDto getSession(@PathVariable Long id) {
        return sessionService.findById(id);
    }

    @PostMapping("/search")
    public List<SessionDto> searchSessions(@RequestBody SearchSessionRequestDto request) {
        throw new IllegalArgumentException("Not implemented");
    }

    @PostMapping("/{sessionId}/attempt")
    public AttemptDto makeAttempt() {
        throw new IllegalArgumentException("Not implemented");
    }

}
