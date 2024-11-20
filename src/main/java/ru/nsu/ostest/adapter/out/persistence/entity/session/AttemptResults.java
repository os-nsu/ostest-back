package ru.nsu.ostest.adapter.out.persistence.entity.session;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.nsu.ostest.domain.common.model.TestResults;

import java.util.List;

@Entity
@Data
public class AttemptResults {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<TestResults> testResults;

    private long duration;

    private String errorDetails;

    @OneToOne(mappedBy = "attemptResults", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Attempt attempt;
}
