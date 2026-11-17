package com.herocode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

/** Knows all the heroes, and puts them in order on the ranking board. */
@Service
public class HeroService {

    // Hero(name, title, bio, strength, speed, intelligence, colorA, colorB, emblem, real)
    // Stats are for fun, not science.
    private final List<Hero> heroes = List.of(
        new Hero("Nova Blaze", "Flame Striker",
            "Turns any spark into a firestorm and never backs down.",
            9, 8, 8, "#ff7a45", "#d9254c", "flame", false),
        new Hero("Titania Steel", "Iron Guardian",
            "Her armor can take a hit from a falling bridge.",
            10, 6, 8, "#6c8cff", "#3a4bb8", "shield", false),
        new Hero("Storm Rider", "Sky Surfer",
            "Rides the lightning from one city to the next.",
            7, 10, 6, "#38d6ff", "#2f5bff", "bolt", false),
        new Hero("Marie Curie", "Pioneer of Radioactivity",
            "Real life: won two Nobel Prizes and discovered the elements polonium and radium.",
            5, 7, 10, "#6ee7b7", "#0f9d78", "atom", true),
        new Hero("Aurora Vance", "Light Bender",
            "Bends light into shields, bridges and disguises.",
            6, 8, 7, "#ffd166", "#f78c6b", "spark", false),
        new Hero("Katherine Johnson", "Human Computer",
            "Real life: NASA mathematician whose orbit calculations helped send astronauts to space.",
            3, 7, 10, "#c4a1ff", "#6d3fd1", "star", true),
        new Hero("Pixel Spark", "Code Breaker",
            "Hacks any lock in the time it takes to blink.",
            3, 8, 8, "#5eead4", "#2563eb", "bolt", false),
        new Hero("Hedy Lamarr", "Actress and Inventor",
            "Real life: her frequency-hopping idea helped inspire technology like Wi-Fi and Bluetooth.",
            3, 6, 9, "#ff9ecb", "#b83280", "wave", true),
        new Hero("Echo Shade", "Shadow Scout",
            "Moves without a sound and is gone before you turn around.",
            5, 9, 3, "#a78bfa", "#312e81", "moon", false),
        new Hero("Ada Lovelace", "First Programmer",
            "Real life: wrote what is often called the first computer program, in the 1840s.",
            2, 4, 10, "#f9a8d4", "#7c3aed", "code", true),
        new Hero("Coral Tide", "Wave Breaker",
            "Commands the ocean, but is not great at puzzles.",
            8, 5, 2, "#2dd4bf", "#0e7490", "wave", false),
        new Hero("Terra Bloom", "Garden Guardian",
            "Grows a forest wall in seconds, slowly and steadily.",
            7, 3, 4, "#86efac", "#15803d", "leaf", false),
        new Hero("Sage Whisper", "Gentle Healer",
            "Calms anyone down and heals what others cannot.",
            2, 5, 6, "#fda4af", "#e11d48", "heart", false),
        new Hero("Luna Drift", "Daydream Walker",
            "Floats between dreams and wakes up in a different place.",
            3, 6, 3, "#93c5fd", "#4338ca", "moon", false)
    );

    public List<Hero> getHeroes() {
        return heroes;
    }

    /**
     * Builds the ranking board. If a player ("you") is given, she is added to the board too.
     * Rank 1 is the strongest hero.
     */
    public List<RankEntry> ranking(Hero you) {
        List<Hero> all = new ArrayList<>(heroes);
        if (you != null) {
            all.add(you);
        }

        // Put the heroes in order, using their power
        all.sort(Comparator.comparingInt(Hero::getPower));

        List<RankEntry> board = new ArrayList<>();
        for (int i = 0; i < all.size(); i++) {
            Hero hero = all.get(i);
            board.add(new RankEntry(i + 1, hero, hero == you));
        }
        return board;
    }

    /** Finds the hero whose power is closest to the player's power. */
    public Hero closestHero(Hero you) {
        Hero best = heroes.get(0);
        for (Hero hero : heroes) {
            int gap = Math.abs(hero.getPower() - you.getPower());
            int bestGap = Math.abs(best.getPower() - you.getPower());
            if (gap < bestGap) {
                best = hero;
            }
        }
        return best;
    }
}
