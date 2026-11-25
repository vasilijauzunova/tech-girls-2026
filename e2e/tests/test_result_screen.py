"""What the player sees after the last answer: their stats and their place on the board."""

from quiz_data import BRAIN_HERO, STRENGTH_HERO, valid_ranks


def test_each_stat_is_shown_on_its_own_row(app):
    """Strength, Speed and Intelligence must each show their own number."""
    app.take_quiz(BRAIN_HERO.answers)

    assert app.result_stats() == {
        "Strength": BRAIN_HERO.strength,
        "Speed": BRAIN_HERO.speed,
        "Intelligence": BRAIN_HERO.intelligence,
    }


def test_the_rank_matches_the_player_power(app, api_heroes):
    """Rank 1 is the strongest, so a player with this power must land among the heroes of equal power."""
    hero_powers = [entry["hero"]["power"] for entry in api_heroes]

    app.take_quiz(STRENGTH_HERO.answers)

    allowed = valid_ranks(STRENGTH_HERO.power, hero_powers)
    assert app.result_rank() in allowed, (
        f"A hero with power {STRENGTH_HERO.power} should be rank {allowed.start}-{allowed.stop - 1}, "
        f"but the page says #{app.result_rank()}"
    )
