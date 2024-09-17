package ru.nsu.ostest.adapter.out.persistence.entity.user;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.nsu.ostest.adapter.out.persistence.entity.group.Group;

@Getter
@Setter
@Entity
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    private String username;

    private String firstname;

    private String secondname;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

}
