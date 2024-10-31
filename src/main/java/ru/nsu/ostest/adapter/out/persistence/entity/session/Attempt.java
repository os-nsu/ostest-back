package ru.nsu.ostest.adapter.out.persistence.entity.session;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.nsu.ostest.domain.common.enums.AttemptStatus;
import ru.nsu.ostest.domain.common.model.TestResults;

import java.util.UUID;

@Getter
@Setter
@Entity
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Attempt {

    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Long order;

    @Enumerated(EnumType.STRING)
    private AttemptStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    private TestResults testResults;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private Session session;
}
