package ru.nsu.ostest.adapter.out.persistence.entity.group;

import jakarta.persistence.*;
import lombok.*;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;

import java.util.ArrayList;
import java.util.List;

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
    private String name;

    @OneToMany(mappedBy = "group", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<User> users = new ArrayList<>();

}
