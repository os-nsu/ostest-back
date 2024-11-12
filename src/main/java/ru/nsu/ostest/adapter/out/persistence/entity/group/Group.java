package ru.nsu.ostest.adapter.out.persistence.entity.group;

import jakarta.persistence.*;
import lombok.*;
import ru.nsu.ostest.adapter.in.rest.model.annotation.DescribableField;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "\"group\"")
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Group {

    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @DescribableField(includeInDescriptor = true, includeInFilter = true)
    @Column(name = "name")
    private String groupName;

    @JoinTable(
            name = "user_group",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<User> users = new HashSet<>();

}
