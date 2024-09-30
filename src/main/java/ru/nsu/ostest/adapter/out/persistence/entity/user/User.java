package ru.nsu.ostest.adapter.out.persistence.entity.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.nsu.ostest.adapter.out.persistence.entity.group.Group;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "\"user\"")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @EqualsAndHashCode.Include
    private String username;

    private String firstName;

    private String secondName;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<UserRole> roles = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, orphanRemoval = true)
    private UserPassword userPassword;

    public void addRole(@NotBlank Role role) {
        UserRole userRoleEntity = new UserRole();
        userRoleEntity.setUser(this);
        userRoleEntity.setRole(role);

        roles = List.of(userRoleEntity);
    }


}
