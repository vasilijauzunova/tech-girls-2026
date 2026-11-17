package com.herocode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class HeroServiceTest {

    private final HeroService service = new HeroService();

    @Test
    void powerIsTheSumOfTheThreeStats() {
        Hero hero = new Hero("Test", "Tester", "", 2, 3, 4, "#000", "#fff", "star", false);
        assertEquals(9, hero.getPower());
    }

    @Test
    void everyStatIsBetween1And10() {
        for (Hero hero : service.getHeroes()) {
            assertTrue(hero.getStrength() >= 1 && hero.getStrength() <= 10, hero.getName() + " strength");
            assertTrue(hero.getSpeed() >= 1 && hero.getSpeed() <= 10, hero.getName() + " speed");
            assertTrue(hero.getIntelligence() >= 1 && hero.getIntelligence() <= 10, hero.getName() + " intelligence");
        }
    }

    @Test
    void rankOneIsTheStrongestHero() {
        List<RankEntry> board = service.ranking(null);

        int strongestPower = 0;
        for (Hero hero : service.getHeroes()) {
            strongestPower = Math.max(strongestPower, hero.getPower());
        }

        assertEquals(1, board.get(0).rank());
        assertEquals(strongestPower, board.get(0).hero().getPower(),
            "Rank #1 should be the strongest hero");
    }

    @Test
    void ranksCountUpFromOne() {
        List<RankEntry> board = service.ranking(null);
        for (int i = 0; i < board.size(); i++) {
            assertEquals(i + 1, board.get(i).rank());
        }
    }

    @Test
    void closestHeroHasTheClosestPower() {
        // Power 25 exactly matches Nova Blaze
        Hero you = new Hero("You", "", "", 9, 8, 8, "#000", "#fff", "star", false);
        assertEquals("Nova Blaze", service.closestHero(you).getName());
    }
}
