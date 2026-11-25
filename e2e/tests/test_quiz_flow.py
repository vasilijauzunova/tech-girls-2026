"""The quiz journey: intro screen -> five questions -> result screen."""

from playwright.sync_api import expect

from quiz_data import STRENGTH_HERO


def test_intro_screen_shows_the_start_button(app, page):
    expect(page).to_have_title("Hero Code Day")
    expect(page.locator("h1")).to_have_text("Which superhero are you?")
    expect(page.locator("#start")).to_be_visible()


def test_playing_the_quiz_shows_the_players_name_on_the_result(app, page):
    app.take_quiz(STRENGTH_HERO.answers, name="Wonder Ada")

    expect(page.locator(".result h1")).to_contain_text("You are most like")
    expect(page.locator(".result-you h2")).to_have_text("Wonder Ada")


def test_a_server_error_shows_a_message_and_a_retry_button(app, page):
    # Pretend the server is broken: this answers the quiz request with an error before the real server can.
    page.route("**/api/quiz", lambda route: route.fulfill(status=500, body="boom"))

    app.start_quiz()
    for _ in range(5):
        app.pick_answer(0)

    expect(page.get_by_role("heading", name="Something went wrong")).to_be_visible()
    page.get_by_role("button", name="Try again").click()
    expect(page.locator("#start")).to_be_visible()
