package ru.nsu.ostestbackend.adapter.in.rest.controller;

import org.springframework.web.bind.annotation.*;
import ru.nsu.ostestbackend.adapter.in.rest.model.session.AttemptDto;
import ru.nsu.ostestbackend.adapter.in.rest.model.session.SearchSessionRequestDto;
import ru.nsu.ostestbackend.adapter.in.rest.model.session.SessionDto;
import ru.nsu.ostestbackend.adapter.in.rest.model.session.StartSessionRequestDto;

import java.util.List;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    @PostMapping("/start")
    public SessionDto startSession(@RequestBody StartSessionRequestDto request) {
        throw new IllegalArgumentException("Not implemented");
    }

    @GetMapping("/{id}")
    public SessionDto getSession(@PathVariable Long id) {
        throw new IllegalArgumentException("Not implemented");
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
