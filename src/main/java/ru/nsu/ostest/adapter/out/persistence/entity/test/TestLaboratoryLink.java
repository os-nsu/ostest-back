package ru.nsu.ostest.adapter.out.persistence.entity.test;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.nsu.ostest.adapter.out.persistence.entity.laboratory.Laboratory;

@Getter
@Setter
@Entity
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TestLaboratoryLink {

    @EmbeddedId
    private TestLaboratoryKey id = new TestLaboratoryKey();

    @ManyToOne
    @MapsId("testId")
    @JoinColumn(name = "test_id")
    private Test test;

    @ManyToOne
    @MapsId("laboratoryId")
    @JoinColumn(name = "laboratory_id")
    private Laboratory laboratory;

    private Boolean isSwitchedOn;

}
