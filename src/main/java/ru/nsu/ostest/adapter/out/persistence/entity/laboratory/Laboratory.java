package ru.nsu.ostest.adapter.out.persistence.entity.laboratory;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Length;
import ru.nsu.ostest.adapter.out.persistence.entity.session.Session;
import ru.nsu.ostest.adapter.out.persistence.entity.test.Test;
import ru.nsu.ostest.adapter.out.persistence.entity.test.TestLaboratoryLink;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
public class Laboratory {

    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = Length.LONG32)
    private String description;

    private Integer semesterNumber;

    private Integer number;

    private OffsetDateTime deadline;

    private Boolean isHidden;

    @OneToMany(mappedBy = "laboratory", orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<TestLaboratoryLink> testsLinks = new ArrayList<>();

    @OneToMany(mappedBy = "laboratory", orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Session> sessions = new ArrayList<>();

    public void addTest(Test test, Boolean isSwitchedOn) {
        TestLaboratoryLink testLaboratoryLink = new TestLaboratoryLink();
        testLaboratoryLink.setTest(test);
        testLaboratoryLink.setLaboratory(this);
        testLaboratoryLink.setIsSwitchedOn(isSwitchedOn);
        testsLinks.add(testLaboratoryLink);
    }
}
