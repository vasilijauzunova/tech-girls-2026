package com.herocode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

class QuizServiceTest {

    private final QuizService quiz = new QuizService();

    @Test
    void thereAreFiveQuestionsWithFourAnswersEach() {
        assertEquals(5, quiz.getQuestions().size());
        for (QuizService.Question question : quiz.getQuestions()) {
            assertEquals(4, question.answers().size());
        }
    }

    @Test
    void pointsOfTheChosenAnswersAreAddedUp() {
        // Answer 0 each time: strength 3+3+3+3+3 = 15, capped at 10. Speed 1+0+0+1+0 = 2. Intelligence 0+1+0+1+1 = 3.
        Hero hero = quiz.buildHero("Sam", List.of(0, 0, 0, 0, 0));
        assertEquals(10, hero.getStrength());
        assertEquals(2, hero.getSpeed());
        assertEquals(3, hero.getIntelligence());
    }

    @Test
    void emptyNameBecomesYou() {
        assertEquals("You", quiz.buildHero("  ", List.of(0, 0, 0, 0, 0)).getName());
    }

    @Test
    void notEnoughAnswersIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> quiz.buildHero("Sam", List.of(0, 1)));
    }

    @Test
    void anAnswerThatDoesNotExistIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> quiz.buildHero("Sam", List.of(0, 0, 0, 0, 9)));
    }
}
