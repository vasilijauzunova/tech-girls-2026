# Hero Code Day: Superhero Power Ranking

Welcome to the team. You are joining a project that is almost finished: a website where you take a
quiz, become a superhero, and see your place on a ranking board.

The client is happy, but **two things are wrong**. One is in the website (what you see), and one is
in the Java code behind it (what the computer works out). Your mission is to find both and fix them.

## Step 1: Start the website

1. At the bottom of VS Code, open the **Terminal** (menu: Terminal > New Terminal).
2. Type this and press Enter:

   ```
   mvn spring-boot:run
   ```
3. Wait until you see `Started HeroApplication`. A popup asks to open the website. Click **Open in Browser**.
   (If you missed it, open the **Ports** tab next to the Terminal and click the globe next to port 8080.)

## Step 2: Be the tester (QA)

1. Take the quiz. Then open the **Ranking board**.
2. Look closely at the page. Does everything make sense? Check the numbers. Check the order.
3. When you find something wrong, **report it as a bug**:
   - Go to your repository on GitHub, open **Issues** and click **New issue**.
   - Choose **Bug report** and fill it in: what you did, what you expected, what you saw.

A good bug report lets someone else repeat the problem in 30 seconds.

## Step 3: Be the developer

Now find *why* it happens in the code. Where to look:

| Folder | What lives there |
|---|---|
| `src/main/java/com/herocode` | The **backend**: the Java code that calculates and sorts |
| `src/main/resources/static` | The **frontend**: the page you see (`app.js` draws it, `style.css` colors it) |
| `src/test/java/com/herocode` | The **tests**: small checks that tell you if the code is right |

Useful things to try:

- Stop the website with `Ctrl + C`, then run the tests: `mvn test`. A failing test tells you something is wrong
  and often **where**.
- Backend (Java) change: stop the website (`Ctrl + C`) and start it again.
- Frontend (page) change: save the file and **refresh the browser**.
- Stuck for 10 minutes? Ask your mentor for a hint card. That is what real developers do too.

## Step 4: Prove it

- `mvn test` shows **all green**.
- Both bugs look right on the website.
- Close your GitHub issues with a short comment on what you changed.

## Bonus missions (if you finish early)

1. **Add your own hero.** In `HeroService.java`, copy a line in the list and make up a new hero with your own
   stats. Where does she land on the board?
2. **Change the rules.** What if intelligence counted double? Change how power is calculated and see who moves up.
   (A test will complain. Why? Fix the test too.)
3. **Make a new quiz question** in `QuizService.java`.
