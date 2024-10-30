package ru.nsu.ostest.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.ostest.adapter.in.rest.model.session.*;
import ru.nsu.ostest.adapter.mapper.AttemptMapper;
import ru.nsu.ostest.adapter.mapper.SessionMapper;
import ru.nsu.ostest.adapter.out.persistence.entity.laboratory.Laboratory;
import ru.nsu.ostest.adapter.out.persistence.entity.session.Attempt;
import ru.nsu.ostest.adapter.out.persistence.entity.session.Session;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;
import ru.nsu.ostest.domain.exception.validation.LaboratoryNotFoundException;
import ru.nsu.ostest.domain.exception.validation.SessionNotFoundException;
import ru.nsu.ostest.domain.exception.validation.UserNotFoundException;
import ru.nsu.ostest.domain.common.enums.AttemptStatus;
import ru.nsu.ostest.domain.exception.validation.AttemptNotFoundException;
import ru.nsu.ostest.domain.exception.validation.SessionNotFoundException;
import ru.nsu.ostest.domain.repository.AttemptRepository;
import ru.nsu.ostest.domain.repository.LaboratoryRepository;
import ru.nsu.ostest.domain.repository.SessionRepository;
import ru.nsu.ostest.domain.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;
    private final LaboratoryRepository laboratoryRepository;
    private final UserRepository userRepository;
    private final AttemptRepository attemptRepository;
    private final AttemptMapper attemptMapper;

    @Transactional
    public SessionDto create(StartSessionRequestDto startSessionRequestDto) {
        User student = userRepository.findById(startSessionRequestDto.studentId())
                .orElseThrow(() -> UserNotFoundException.notFoundUserWithId(startSessionRequestDto.studentId()));
        Laboratory laboratory = laboratoryRepository.findById(startSessionRequestDto.laboratoryId())
                .orElseThrow(() -> LaboratoryNotFoundException
                        .notFoundLaboratoryWithId(startSessionRequestDto.laboratoryId()));

        Session session = new Session();
        session.setStudent(student);
        session.setLaboratory(laboratory);

        session = sessionRepository.save(session);
        return sessionMapper.sessionToSessionDto(session);
    }

    public SessionDto findById(Long id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> SessionNotFoundException.notFoundSessionWithId(id));

        return sessionMapper.sessionToSessionDto(session);
    }

    public SessionDto getLabSessionForStudent(GetLabSessionFromStudentRequestDto getLabSessionFromStudentRequestDto) {
        Long studentId = getLabSessionFromStudentRequestDto.studentId();
        Long laboratoryId = getLabSessionFromStudentRequestDto.laboratoryId();
        Session session = sessionRepository.getSessionByStudentIdAndLaboratoryId(studentId, laboratoryId);
        return sessionMapper.sessionToSessionDto(session);
    }

    public List<SessionDto> getUserSessions(Long userId) {
        return sessionMapper.sessionToSessionDto(sessionRepository.getSessionByStudentIdOrTeacherId(userId, userId));
    }

    @Transactional
    public AttemptDto makeAttempt(MakeAttemptDto makeAttemptDto, Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> SessionNotFoundException.notFoundSessionWithId(sessionId));
        Attempt attempt = attemptMapper.makeAttemptDtoToAttempt(makeAttemptDto);
        attempt = session.makeAttempt(attempt);
        attempt = attemptRepository.save(attempt);
        return attemptMapper.attemptToAttemptDto(attempt);
    }

    public AttemptDto findAttemptById(UUID id) {
        return attemptMapper.attemptToAttemptDto(attemptRepository.findById(id)
                .orElseThrow(() -> AttemptNotFoundException.notFoundAttemptWithId(id)));
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
}
