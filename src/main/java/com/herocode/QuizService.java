package com.herocode;

import java.util.List;

import org.springframework.stereotype.Service;

/** The quiz questions, and the code that turns the player's answers into a hero. */
@Service
public class QuizService {

    /** One possible answer. The numbers are the points it gives to each stat. */
    public record Answer(String text, int strength, int speed, int intelligence) {
    }

    public record Question(String text, List<Answer> answers) {
    }

    private static final int MAX_STAT = 10;

    private final List<Question> questions = List.of(
        new Question("It's Saturday. What sounds like the best plan?", List.of(
            new Answer("Beat my personal best at a sports challenge", 3, 1, 0),
            new Answer("Race my friends across town on bikes", 1, 3, 0),
            new Answer("Solve an escape room", 0, 1, 3),
            new Answer("A bit of everything with my crew", 2, 2, 2))),
        new Question("A big problem shows up at school. What do you do first?", List.of(
            new Answer("Step in and protect everyone", 3, 0, 1),
            new Answer("Act right away, before anyone else", 0, 3, 1),
            new Answer("Study it and make a plan", 1, 0, 3),
            new Answer("Ask the group for ideas", 1, 1, 2))),
        new Question("Pick a superpower.", List.of(
            new Answer("Super strength", 3, 0, 0),
            new Answer("Teleportation", 0, 3, 0),
            new Answer("Reading minds", 0, 1, 3),
            new Answer("Controlling time", 0, 2, 2))),
        new Question("Your friends would say you are...", List.of(
            new Answer("Brave", 3, 1, 1),
            new Answer("Quick", 1, 3, 1),
            new Answer("Brilliant", 1, 1, 3),
            new Answer("Dependable", 2, 1, 2))),
        new Question("Which gadget do you take on a mission?", List.of(
            new Answer("Power gloves", 3, 0, 1),
            new Answer("Jet boots", 1, 3, 0),
            new Answer("Smart glasses", 0, 1, 3),
            new Answer("A utility belt with everything", 1, 2, 2)))
    );

    public List<Question> getQuestions() {
        return questions;
    }

    /**
     * Adds up the points of the chosen answers and makes a new hero from them.
     * chosen.get(0) is the answer picked for question 1 (0 = first answer), and so on.
     */
    public Hero buildHero(String playerName, List<Integer> chosen) {
        if (chosen == null || chosen.size() != questions.size()) {
            throw new IllegalArgumentException("Please answer all " + questions.size() + " questions.");
        }

        int strength = 0;
        int speed = 0;
        int intelligence = 0;
        for (int i = 0; i < questions.size(); i++) {
            List<Answer> options = questions.get(i).answers();
            int pick = chosen.get(i);
            if (pick < 0 || pick >= options.size()) {
                throw new IllegalArgumentException("Answer " + pick + " does not exist for question " + (i + 1) + ".");
            }
            strength += options.get(pick).strength();
            speed += options.get(pick).speed();
            intelligence += options.get(pick).intelligence();
        }

        // A stat can never go above 10
        strength = Math.min(MAX_STAT, strength);
        speed = Math.min(MAX_STAT, speed);
        intelligence = Math.min(MAX_STAT, intelligence);

        String name = cleanName(playerName);

        // The strongest stat decides how the player's picture looks
        if (strength >= speed && strength >= intelligence) {
            return new Hero(name, "Force of Nature", "Your own hero, made by the quiz.",
                strength, speed, intelligence, "#ff8a5c", "#c2255c", "shield", false);
        } else if (speed >= intelligence) {
            return new Hero(name, "Lightning Mind", "Your own hero, made by the quiz.",
                strength, speed, intelligence, "#4fd1ff", "#3b5bff", "bolt", false);
        } else {
            return new Hero(name, "Master Strategist", "Your own hero, made by the quiz.",
                strength, speed, intelligence, "#b794ff", "#5b3fd1", "star", false);
        }
    }

    private String cleanName(String playerName) {
        if (playerName == null || playerName.isBlank()) {
            return "You";
        }
        String trimmed = playerName.trim();
        return trimmed.length() > 20 ? trimmed.substring(0, 20) : trimmed;
    }
}
