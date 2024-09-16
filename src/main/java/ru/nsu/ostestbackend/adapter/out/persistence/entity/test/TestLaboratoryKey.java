package ru.nsu.ostestbackend.adapter.out.persistence.entity.test;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class TestLaboratoryKey implements Serializable {

    private Long testId;
    private Long laboratoryId;

}
