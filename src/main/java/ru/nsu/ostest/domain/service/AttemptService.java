package ru.nsu.ostest.domain.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.ostest.adapter.in.rest.model.session.AttemptDto;
import ru.nsu.ostest.adapter.in.rest.model.session.AvailableTaskResponse;
import ru.nsu.ostest.adapter.in.rest.model.session.MakeAttemptDto;
import ru.nsu.ostest.adapter.in.rest.model.test.AttemptResultSetRequest;
import ru.nsu.ostest.adapter.in.rest.model.test.AttemptResultSetResponse;
import ru.nsu.ostest.adapter.mapper.AttemptMapper;
import ru.nsu.ostest.adapter.out.persistence.entity.session.Attempt;
import ru.nsu.ostest.adapter.out.persistence.entity.session.AttemptResults;
import ru.nsu.ostest.adapter.out.persistence.entity.session.Session;
import ru.nsu.ostest.domain.common.enums.AttemptStatus;
import ru.nsu.ostest.domain.common.enums.SessionStatus;
import ru.nsu.ostest.domain.exception.validation.AttemptNotFoundException;
import ru.nsu.ostest.domain.exception.validation.SessionNotFoundException;
import ru.nsu.ostest.domain.repository.AttemptRepository;
import ru.nsu.ostest.domain.repository.AttemptResultsRepository;
import ru.nsu.ostest.domain.repository.SessionRepository;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AttemptService {
    private final AttemptRepository attemptRepository;
    private final AttemptResultsRepository attemptResultsRepository;
    private final AttemptMapper attemptMapper;
    private final SessionRepository sessionRepository;

    @Transactional
    public AttemptDto makeAttempt(MakeAttemptDto makeAttemptDto, Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> SessionNotFoundException.notFoundSessionWithId(sessionId));
        Attempt attempt = attemptMapper.makeAttemptDtoToAttempt(makeAttemptDto);
        attempt = session.makeAttempt(attempt);
        attempt = attemptRepository.save(attempt);
        if (session.getStatus().equals(SessionStatus.NEW)) {
            session.setStatus(SessionStatus.IN_PROGRESS);
            sessionRepository.save(session);
        }
        return attemptMapper.attemptToAttemptDto(attempt);
    }

    @Transactional
    public AvailableTaskResponse getAvailableTask() {
        Attempt attempt =
                attemptRepository.findFirstByStatusOrderByCreatedAtAsc(AttemptStatus.IN_QUEUE).orElse(null);
        if (attempt == null) {
            return AvailableTaskResponse.unavailableTaskResponse();
        }
        attempt.setStatus(AttemptStatus.IN_PROGRESS);
        attempt = attemptRepository.save(attempt);
        return attemptMapper.toAvailableTaskResponse(attempt);
    }

    private AttemptStatus determineAttemptStatus(AttemptResultSetRequest request) {
        if (Boolean.TRUE.equals(request.getIsError())) {
            return AttemptStatus.ERROR;
        }
        return Boolean.TRUE.equals(request.getIsPassed()) ? AttemptStatus.SUCCESS : AttemptStatus.FAILURE;
    }

    private SessionStatus determineSessionStatus(AttemptResultSetRequest request) {

        return Boolean.TRUE.equals(request.getIsPassed()) ? SessionStatus.SUCCESS : SessionStatus.FAILURE;
    }

    @Transactional
    public AttemptResultSetResponse saveAttemptResult(AttemptResultSetRequest request) {
        Attempt attempt = findAttemptById(request.getId());
        AttemptStatus attemptStatus = determineAttemptStatus(request);
        SessionStatus sessionStatus = determineSessionStatus(request);
        attempt.setStatus(attemptStatus);
        attempt.getSession().setStatus(sessionStatus);

        AttemptResults attemptResults = attemptMapper.attemptResultSetRequestToAttemptResults(request, attempt);
        attempt.setAttemptResults(attemptResults);
        attemptRepository.save(attempt);
        return new AttemptResultSetResponse(attempt.getId());
    }

    public AttemptDto getAttemptById(UUID id) {
        return attemptMapper.attemptToAttemptDto(findAttemptById(id));
    }

    public Attempt findAttemptById(UUID id) {
        return attemptRepository.findById(id)
                .orElseThrow(() -> AttemptNotFoundException.notFoundAttemptWithId(id));
    }

}
