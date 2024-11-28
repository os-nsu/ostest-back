package ru.nsu.ostest.adapter.in.rest.model.user.userData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentRatingDTO {
    private Long studentId;
    private String username;
    private String firstName;
    private String secondName;
    private Long completedLabs;
}
