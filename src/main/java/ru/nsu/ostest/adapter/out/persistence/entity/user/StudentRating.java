package ru.nsu.ostest.adapter.out.persistence.entity.user;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "student_lab_ratings")
public class StudentRating {
    @Id
    private Long studentId;
    private String username;
    private String firstName;
    private String secondName;
    private Long completedLabs;
}