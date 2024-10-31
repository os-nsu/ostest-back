package ru.nsu.ostest.adapter.out.persistence.entity.session;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.nsu.ostest.adapter.out.persistence.entity.laboratory.Laboratory;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;
import ru.nsu.ostest.domain.common.enums.AttemptStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Session {

    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @ManyToOne
    @JoinColumn(name = "laboratory_id")
    private Laboratory laboratory;

    @OneToMany(mappedBy = "session", orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Attempt> attempts = new ArrayList<>();

    public Attempt makeAttempt() {
        Attempt attempt = new Attempt();
        attempt.setSession(this);
        attempt.setStatus(AttemptStatus.IN_QUEUE);
        if (attempts.isEmpty()) {
            attempt.setOrder(1L);
        } else {
            attempt.setOrder(attempts.getLast().getOrder() + 1L);
        }
        attempts.add(attempt);
        return attempt;
    }
}
