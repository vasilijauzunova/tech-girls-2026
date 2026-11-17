package com.herocode;

/** One line of the ranking board: a place (rank), a hero, and whether that hero is the player. */
public record RankEntry(int rank, Hero hero, boolean you) {
}
