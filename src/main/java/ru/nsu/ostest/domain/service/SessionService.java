package ru.nsu.ostest.domain.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.ostest.adapter.in.rest.model.session.GetLabSessionFroStudentRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.session.SessionDto;
import ru.nsu.ostest.adapter.in.rest.model.session.StartSessionRequestDto;
import ru.nsu.ostest.adapter.mapper.SessionMapper;
import ru.nsu.ostest.adapter.out.persistence.entity.laboratory.Laboratory;
import ru.nsu.ostest.adapter.out.persistence.entity.session.Session;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;
import ru.nsu.ostest.domain.repository.LaboratoryRepository;
import ru.nsu.ostest.domain.repository.SessionRepository;
import ru.nsu.ostest.domain.repository.UserRepository;

import java.util.List;

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
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Laboratory laboratory = laboratoryRepository.findById(startSessionRequestDto.laboratoryId())
                .orElseThrow(() -> new EntityNotFoundException("Laboratory not found"));

        Session session = new Session();
        session.setStudent(student);
        session.setLaboratory(laboratory);

        session = sessionRepository.save(session);
        return sessionMapper.sessionToSessionDto(session);
    }

    public SessionDto findById(Long id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        return sessionMapper.sessionToSessionDto(session);
    }

    public SessionDto getLabSessionForStudent(GetLabSessionFroStudentRequestDto getLabSessionFroStudentRequestDto) {
        Long studentId = getLabSessionFroStudentRequestDto.studentId();
        Long laboratoryId = getLabSessionFroStudentRequestDto.laboratoryId();
        Session session = sessionRepository.getSessionByStudentIdAndLaboratoryId(studentId, laboratoryId);
        return sessionMapper.sessionToSessionDto(session);
    }

    public List<SessionDto> getUserSessions(Long userId) {
        return sessionMapper.sessionToSessionDto(sessionRepository.getSessionByStudentIdOrTeacherId(userId, userId));
    }
}
