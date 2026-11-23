# Template

**Summary:** One sentence: what is wrong.

**Environment:** App URL, browser, screen (Welcome / Question / Result / Ranking board). Add the hero name and answers if you took the quiz.

**Description:** Two to three sentences: where and how the error happens.

**Steps to reproduce:**
1. Open the app
2. ...

**Expected Result:** What should happen.

**Actual Result:** What happens instead.

**Severity:** Critical / Major / Minor / Trivial
- **Critical:** The app is unusable or crashes, and there is no workaround.
- **Major:** An important feature gives wrong results, but the app still works.
- **Minor:** A small feature or edge case is wrong, and there is an easy workaround.
- **Trivial:** Cosmetic issue, such as a typo, with no effect on how the app works.

**Screenshot (optional):** Paste an image of the screen where the error is visible.

**Note (optional):** Your idea of the cause. Tip: open the API address (e.g. `/api/heroes`) in the browser. If the data is wrong there too, the cause is probably in the back-end.

--------------------
# Example

**Summary:** Hero ordering on the ranking board is incorrect

**Environment:** http://localhost:8080, Chrome on macOS, Ranking board

**Description:** When the user opens the ranking board, the hero with the lowest power is shown as #1, although the page says "Strongest first". The same order is in the JSON from `GET /api/heroes`.

**Steps to reproduce:**
1. Open the hero application [http://localhost:8080]
2. Click "Ranking board" in the top menu

**Expected Result:** The heroes are ranked from strongest to weakest.

**Actual Result:** The heroes are ranked from weakest to strongest.

**Severity:** Major

**Screenshot:** ranking_board_order.png

**Note:** Since the same order is in the JSON data, the incorrect sorting is probably happening on the back-end side.
