package ru.nsu.ostest.domain.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nsu.ostest.adapter.in.rest.model.user.userData.StudentRatingDTO;
import ru.nsu.ostest.adapter.mapper.StudentsRatingMapper;
import ru.nsu.ostest.adapter.out.persistence.entity.user.StudentRating;
import ru.nsu.ostest.domain.repository.RatingRepository;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final StudentsRatingMapper mapper;

    public List<StudentRatingDTO> getRating(Integer topN) {
        if (topN == null || topN <= 0) {
            topN = Integer.MAX_VALUE;
        }

        List<StudentRating> students = ratingRepository.findTopNStudents(topN);
        return students.stream()
                .map(mapper::mapToStudentRatingDTO)
                .toList();
    }
}
