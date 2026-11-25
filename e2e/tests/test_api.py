"""The JSON API behind the website, called directly (no browser page). Fast, and it shows whether a
problem lives in the backend or in the page."""


def test_heroes_are_sorted_from_strongest_to_weakest(api_heroes):
    powers = [entry["hero"]["power"] for entry in api_heroes]

    assert powers == sorted(powers, reverse=True)


def test_a_quiz_with_missing_answers_is_rejected_with_400(api):
    response = api.post("/api/quiz", data={"name": "Sam", "answers": [0, 1]})

    assert response.status == 400
