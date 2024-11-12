package ru.nsu.ostest.adapter.out.persistence.entity.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.nsu.ostest.adapter.in.rest.model.annotation.DescribableField;
import ru.nsu.ostest.adapter.out.persistence.entity.group.Group;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @DescribableField(includeInDescriptor = true, includeInFilter = true)
    private String username;

    @DescribableField(includeInDescriptor = true, includeInFilter = true)
    private String firstName;

    @DescribableField(includeInDescriptor = true, includeInFilter = true)
    private String secondName;


    @JoinTable(
            name = "user_group",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @DescribableField(includeInDescriptor = true, includeInFilter = true)
    private Set<Group> groups = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @DescribableField(includeInDescriptor = true, includeInFilter = true)
    private List<UserRole> roles = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, orphanRemoval = true)
    private UserPassword userPassword;

    public void addRole(@NotBlank Role role) {
        UserRole userRoleEntity = new UserRole();
        userRoleEntity.setUser(this);
        userRoleEntity.setRole(role);

        roles.add(userRoleEntity);
    }


}
