package com.herocode;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * The web address list (API) of our app. The website asks these addresses for data.
 *   GET  /api/heroes     the ranking board
 *   GET  /api/questions  the quiz questions
 *   POST /api/quiz       send answers, get back your hero and your place on the board
 */
@RestController
@RequestMapping("/api")
public class HeroController {

    /** What the website sends after the quiz. */
    public record QuizRequest(String name, List<Integer> answers) {
    }

    /** What we send back. */
    public record QuizResult(Hero you, int rank, int total, Hero closest, List<RankEntry> ranking) {
    }

    private final HeroService heroService;
    private final QuizService quizService;

    public HeroController(HeroService heroService, QuizService quizService) {
        this.heroService = heroService;
        this.quizService = quizService;
    }

    @GetMapping("/heroes")
    public List<RankEntry> heroes() {
        return heroService.ranking(null);
    }

    @GetMapping("/questions")
    public List<QuizService.Question> questions() {
        return quizService.getQuestions();
    }

    @PostMapping("/quiz")
    public QuizResult quiz(@RequestBody QuizRequest request) {
        Hero you;
        try {
            you = quizService.buildHero(request.name(), request.answers());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        List<RankEntry> board = heroService.ranking(you);
        int rank = 0;
        for (RankEntry entry : board) {
            if (entry.you()) {
                rank = entry.rank();
            }
        }
        return new QuizResult(you, rank, board.size(), heroService.closestHero(you), board);
    }
}
