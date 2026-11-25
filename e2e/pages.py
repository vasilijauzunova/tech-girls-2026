"""Page object for the Hero Code Day website.

A page object hides the page's selectors behind readable methods, so the tests read like a story
(`app.take_quiz(...)`) and a markup change is fixed in one place.
"""

from dataclasses import dataclass

from playwright.sync_api import Locator, Page, expect


@dataclass
class Card:
    """One hero card on the ranking board."""

    rank: int
    name: str
    power: int
    strength: int
    speed: int
    intelligence: int


class HeroApp:
    def __init__(self, page: Page, base_url: str):
        self.page = page
        self.base_url = base_url

    # ---------- opening and navigating ----------

    def open(self) -> None:
        self.page.goto(self.base_url)
        expect(self.page.locator("#start")).to_be_visible()

    def open_board(self) -> None:
        """Click 'Ranking board' in the top bar and wait until the cards are there."""
        self.page.locator("#nav-board").click()
        self.wait_for_board()

    def wait_for_board(self) -> None:
        expect(self.page.locator(".board h1")).to_have_text("Ranking board")
        expect(self.page.locator(".board .card").first).to_be_visible()

    # ---------- the quiz ----------

    def start_quiz(self, name: str | None = None) -> None:
        if name is not None:
            self.page.locator("#player-name").fill(name)
        self.page.locator("#start").click()
        expect(self.page.locator(".answer").first).to_be_visible()

    def pick_answer(self, index: int) -> None:
        self.page.locator(f'.answer[data-index="{index}"]').click()

    def take_quiz(self, answers: list[int], name: str | None = None) -> None:
        """Play the whole quiz and wait for the result screen."""
        self.start_quiz(name)
        for index in answers:
            self.pick_answer(index)
        expect(self.page.locator(".panel.result")).to_be_visible()

    # ---------- the result screen ----------

    def result_stats(self) -> dict[str, int]:
        """The player's stats as drawn on the result screen, e.g. {'Strength': 2, 'Speed': 4, ...}."""
        return self._read_stats(self.page.locator(".result-you"))

    def result_rank(self) -> int:
        return int(self.page.locator(".big-rank").inner_text().lstrip("#"))  # "#12" -> 12

    # ---------- the ranking board ----------

    def cards(self) -> list[Card]:
        cards = []
        for card in self.page.locator(".board .card").all():
            stats = self._read_stats(card)
            cards.append(
                Card(
                    rank=int(card.locator(".rank").inner_text().lstrip("#")),
                    # The <h3> also holds badges ("Real-life hero"), so read only its first text node.
                    name=card.locator("h3").evaluate("h3 => h3.firstChild.textContent.trim()"),
                    power=int(card.locator(".power-num").inner_text()),
                    strength=stats["Strength"],
                    speed=stats["Speed"],
                    intelligence=stats["Intelligence"],
                )
            )
        return cards

    # ---------- helpers ----------

    @staticmethod
    def _read_stats(container: Locator) -> dict[str, int]:
        """Read the 'Strength / Speed / Intelligence' rows inside a result panel or a card."""
        rows = container.locator(".stat")
        return {
            rows.nth(i).locator(".stat-label").inner_text(): int(rows.nth(i).locator(".stat-num").inner_text())
            for i in range(rows.count())
        }
