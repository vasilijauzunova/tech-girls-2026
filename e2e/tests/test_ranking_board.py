"""The ranking board: every hero on a card, strongest first, with numbers that add up."""

import pytest


@pytest.fixture
def board(app):
    """The ranking board, opened from the top bar."""
    app.open_board()
    return app


def test_every_hero_has_a_card(board, api_heroes):
    names_on_page = sorted(card.name for card in board.cards())
    names_from_api = sorted(entry["hero"]["name"] for entry in api_heroes)

    assert names_on_page == names_from_api


def test_the_strongest_hero_is_listed_first(board):
    cards = board.cards()

    assert cards[0].power == max(card.power for card in cards)


def test_power_is_the_sum_of_the_stats_on_every_card(board):
    for card in board.cards():
        total = card.strength + card.speed + card.intelligence
        assert card.power == total, (
            f"{card.name}: power {card.power}, but the card shows strength {card.strength} + "
            f"speed {card.speed} + intelligence {card.intelligence} = {total}"
        )
