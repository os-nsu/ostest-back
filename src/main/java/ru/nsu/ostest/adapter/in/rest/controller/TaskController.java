package ru.nsu.ostest.adapter.in.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nsu.ostest.adapter.in.rest.model.session.AvailableTaskResponse;
import ru.nsu.ostest.adapter.in.rest.model.test.AttemptResultSetRequest;
import ru.nsu.ostest.adapter.in.rest.model.test.AttemptResultSetResponse;
import ru.nsu.ostest.domain.service.AttemptService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task")
public class TaskController {

    private final AttemptService attemptService;

    @GetMapping("/available")
    public AvailableTaskResponse getTask() {
        return attemptService.getAvailableTask();
    }

    @PostMapping("/result")
    public ResponseEntity<AttemptResultSetResponse> saveResult(@RequestBody AttemptResultSetRequest request) {
        AttemptResultSetResponse response = attemptService.saveAttemptResult(request);
        return ResponseEntity.ok(response);

    }
}
