package ru.nsu.ostestbackend.adapter.out.persistence.entity.laboratory;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.nsu.ostestbackend.adapter.out.persistence.entity.session.Session;
import ru.nsu.ostestbackend.adapter.out.persistence.entity.test.TestLaboratoryLink;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Laboratory {

    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private LocalDateTime deadline;

    private Boolean isHidden;

    @OneToMany(mappedBy = "laboratory", orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<TestLaboratoryLink> testsLinks  = new ArrayList<>();

    @OneToMany(mappedBy = "laboratory", orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Session> sessions = new ArrayList<>();

}
