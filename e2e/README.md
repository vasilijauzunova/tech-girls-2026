# End-to-end tests (Playwright + Python)

A small set of browser tests that play the real website, plus two direct API checks. Built for learning.

## Folder layout

```
e2e/
├── tests/                  <- the tests (start reading here)
│   ├── test_quiz_flow.py
│   ├── test_result_screen.py
│   ├── test_ranking_board.py
│   └── test_api.py
├── conftest.py             <- shared setup: starts the app, opens the browser, writes the report
├── pages.py                <- "page object": readable methods like app.take_quiz(...)
├── quiz_data.py            <- quiz answer sets and the stats they should produce
├── pytest.ini              <- test settings
└── requirements.txt        <- Python packages
```

## One-time setup

Needs Python 3.9+, Java 17 and Maven (the same as the project itself).

```
cd e2e
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
playwright install chromium
```

## Run

```
cd e2e
source .venv/bin/activate
pytest                                  # all tests, headless
pytest --headed --slowmo 400            # watch the browser
pytest tests/test_api.py                # one file
pytest -k board                         # only tests with "board" in the name
```

The suite starts its own copy of the app (`mvn spring-boot:run`) on a free port and stops it at the end, so
it does not matter whether the website is already running on port 8080. To test an app that is already
running instead: `E2E_BASE_URL=http://localhost:8080 pytest`.

## The report

Every run writes `e2e/report.html` (the results and the failure message of each red test) plus an
`e2e/assets/` folder with a screenshot of each failing page. It works offline (`open report.html` on macOS).
Click a screenshot in the report to open it full size. Both are overwritten on every run and are not committed.

Keep `report.html`, `assets/` and `test-results/` together (the report links to files in both): if you share or
move the report, take the folders with it.
(We do not use `--self-contained-html` because browsers refuse to open the embedded images when you click them.)

For a closer look at a failed test, a trace is kept in `e2e/test-results/<test>/trace.zip`. Open it with
`playwright show-trace test-results/<test>/trace.zip` to step through every click.

## What the tests do

| Test | Shows how to... | Catches |
|---|---|---|
| `test_quiz_flow.py::test_intro_screen_shows_the_start_button` | check what is on a page (`expect`) | |
| `test_quiz_flow.py::test_playing_the_quiz_...result` | click through a whole flow | |
| `test_quiz_flow.py::test_a_server_error_...retry_button` | fake a broken server (`page.route`) | |
| `test_result_screen.py::test_each_stat_is_shown_on_its_own_row` | compare the page with numbers worked out by hand | Intelligence bug |
| `test_result_screen.py::test_the_rank_matches_the_player_power` | accept a range when ties are possible | Ranking bug |
| `test_ranking_board.py::test_every_hero_has_a_card` | compare the page with the API | |
| `test_ranking_board.py::test_the_strongest_hero_is_listed_first` | check order | Ranking bug |
| `test_ranking_board.py::test_power_is_the_sum_of_the_stats_on_every_card` | check a rule on every card | Intelligence bug |
| `test_api.py::test_heroes_are_sorted_from_strongest_to_weakest` | test the backend without a browser | Ranking bug |
| `test_api.py::test_a_quiz_with_missing_answers_is_rejected_with_400` | check an error response | |

## Reading the results

The tests describe how the app **should** work, so a red test means a real bug, not a broken test.
As the project stands, the two bugs from the main README make 5 of the 10 tests fail:

- **Ranking order** (backend, `HeroService.ranking`): the weakest hero is rank #1.
  Red: `strongest_hero_is_listed_first`, `rank_matches_the_player_power`, `heroes_are_sorted...`.
- **Intelligence row** (frontend, `app.js` `statRows`): it shows the Speed number.
  Red: `each_stat_is_shown_on_its_own_row`, `power_is_the_sum_of_the_stats_on_every_card`.

Fix both and all 10 go green.

## Good to know

- Expected quiz results (`quiz_data.py`) are worked out by hand from the points in `QuizService`.
  If you change a question's points, update that file.
- The tests read the hero list from the API instead of hard-coding it, so adding a hero (bonus mission)
  does not break them.
- Google Fonts are stubbed out and reduced motion is on, so the tests run offline and fast.
