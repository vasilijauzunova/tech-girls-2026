"""Quiz answer sets with their expected stats, worked out by hand from the points in QuizService.

Each set picks the same answer position for every question, which keeps the arithmetic easy to check:

  all first answers  -> strength 3+3+3+3+3 = 15 (a stat is capped at 10), speed 1+0+0+1+0 = 2, intelligence 0+1+0+1+1 = 3
  all third answers  -> strength 0+1+0+1+0 = 2, speed 1+0+1+1+1 = 4, intelligence 3+3+3+3+3 = 15 (capped to 10)

In both sets the three stats are different from each other, so a page that shows the wrong stat
in the wrong row is always noticed.
"""

from dataclasses import dataclass


@dataclass(frozen=True)
class Outcome:
    answers: list[int]
    strength: int
    speed: int
    intelligence: int

    @property
    def power(self) -> int:
        return self.strength + self.speed + self.intelligence


STRENGTH_HERO = Outcome([0, 0, 0, 0, 0], strength=10, speed=2, intelligence=3)
BRAIN_HERO = Outcome([2, 2, 2, 2, 2], strength=2, speed=4, intelligence=10)


def valid_ranks(my_power: int, hero_powers: list[int]) -> range:
    """The ranks a player may legally get on a strongest-first board.

    Heroes with the same power can be listed in either order, so a player who ties with a hero
    may sit on either side of it. (Hero powers run 12-25 and the lowest power a player can reach is 15,
    so a tie always happens.)
    """
    stronger = sum(1 for p in hero_powers if p > my_power)
    tied = sum(1 for p in hero_powers if p == my_power)
    return range(stronger + 1, stronger + tied + 2)
