package com.herocode;

/**
 * One superhero. Strength, speed and intelligence are numbers from 1 to 10.
 * The colors and the emblem are only used to draw the hero's picture on the website.
 */
public class Hero {

    private final String name;
    private final String title;
    private final String bio;
    private final int strength;
    private final int speed;
    private final int intelligence;
    private final String colorA;
    private final String colorB;
    private final String emblem;
    private final boolean real; // true for real-life heroes of science

    public Hero(String name, String title, String bio,
                int strength, int speed, int intelligence,
                String colorA, String colorB, String emblem, boolean real) {
        this.name = name;
        this.title = title;
        this.bio = bio;
        this.strength = strength;
        this.speed = speed;
        this.intelligence = intelligence;
        this.colorA = colorA;
        this.colorB = colorB;
        this.emblem = emblem;
        this.real = real;
    }

    /** Power is the sum of the three stats. */
    public int getPower() {
        return strength + speed + intelligence;
    }

    public String getName() { return name; }
    public String getTitle() { return title; }
    public String getBio() { return bio; }
    public int getStrength() { return strength; }
    public int getSpeed() { return speed; }
    public int getIntelligence() { return intelligence; }
    public String getColorA() { return colorA; }
    public String getColorB() { return colorB; }
    public String getEmblem() { return emblem; }
    public boolean isReal() { return real; }
}
