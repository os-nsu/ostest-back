package ru.nsu.ostest.adapter.in.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.ostest.adapter.in.rest.model.user.userData.StudentRatingDTO;
import ru.nsu.ostest.domain.service.RatingService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/rating")
public class RatingController {

    private final RatingService ratingService;

    @GetMapping
    public List<StudentRatingDTO> getRating(@RequestParam(value = "topN", required = false) Integer topN) {
        return ratingService.getRating(topN);
    }
}