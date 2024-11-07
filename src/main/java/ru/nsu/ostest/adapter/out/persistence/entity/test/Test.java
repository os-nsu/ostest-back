package ru.nsu.ostest.adapter.out.persistence.entity.test;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Length;
import ru.nsu.ostest.domain.common.enums.TestCategory;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Test {

    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = Length.LONG32)
    private String code;

    private String description;

    @Enumerated(EnumType.STRING)
    private TestCategory category;

    private byte[] scriptBody;

    @OneToMany(mappedBy = "test", orphanRemoval = true)
    private List<TestLaboratoryLink> laboratoriesLinks  = new ArrayList<>();

}
