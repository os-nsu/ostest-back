package ru.nsu.ostest.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.ostest.adapter.in.rest.model.session.*;
import ru.nsu.ostest.adapter.mapper.SessionMapper;
import ru.nsu.ostest.adapter.out.persistence.entity.laboratory.Laboratory;
import ru.nsu.ostest.adapter.out.persistence.entity.session.Session;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;
import ru.nsu.ostest.domain.common.enums.SessionStatus;
import ru.nsu.ostest.domain.exception.validation.LaboratoryNotFoundException;
import ru.nsu.ostest.domain.exception.validation.SessionNotFoundException;
import ru.nsu.ostest.domain.exception.validation.UserNotFoundException;
import ru.nsu.ostest.domain.repository.LaboratoryRepository;
import ru.nsu.ostest.domain.repository.SessionRepository;
import ru.nsu.ostest.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;
    private final LaboratoryRepository laboratoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public SessionDto create(StartSessionRequestDto startSessionRequestDto) {
        User student = userRepository.findById(startSessionRequestDto.studentId())
                .orElseThrow(() -> UserNotFoundException.notFoundUserWithId(startSessionRequestDto.studentId()));
        Laboratory laboratory = laboratoryRepository.findById(startSessionRequestDto.laboratoryId())
                .orElseThrow(() -> LaboratoryNotFoundException
                        .notFoundLaboratoryWithId(startSessionRequestDto.laboratoryId()));

        Session session = new Session();
        session.setStudent(student);
        session.setStatus(SessionStatus.NEW);
        session.setLaboratory(laboratory);

        session = sessionRepository.save(session);
        return sessionMapper.sessionToSessionDto(session);
    }

    public SessionDto findById(Long id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> SessionNotFoundException.notFoundSessionWithId(id));

        return sessionMapper.sessionToSessionDto(session);
    }

    public SessionShortDto getLabSessionForStudent(GetLabSessionFromStudentRequestDto getLabSessionFromStudentRequestDto) {
        Long studentId = getLabSessionFromStudentRequestDto.studentId();
        Long laboratoryId = getLabSessionFromStudentRequestDto.laboratoryId();
        Session session = sessionRepository.getSessionByStudentIdAndLaboratoryId(studentId, laboratoryId);
        return sessionMapper.sessionToSessionShortDto(session);
    }

    public Page<SessionShortDto> getUserSessions(Long userId, Pageable pageRequest) {
        return sessionRepository.getSessionByStudentIdOrTeacherId(userId, userId, pageRequest)
                .map(sessionMapper::sessionToSessionShortDto);
    }
}
