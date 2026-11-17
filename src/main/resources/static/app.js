// The website. It asks the Java backend for data (fetch) and draws it on the page.

const app = document.getElementById('app');

let questions = [];   // the quiz questions, from /api/questions
let current = 0;      // which question we are on
let choices = [];     // the answer picked for each question (0 = first answer)
let playerName = '';  // the name typed on the first screen

// ---------- small helpers ----------

function escapeHtml(text) {
  return String(text)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

// One row with a label, a bar and a number, for example "Speed  ▇▇▇▇▇▁▁▁▁▁  8"
function statRow(label, value) {
  return `
    <div class="stat">
      <span class="stat-label">${label}</span>
      <div class="bar"><div class="fill" style="width:${value * 10}%"></div></div>
      <span class="stat-num">${value}</span>
    </div>`;
}

// The three stat rows of a hero
function statRows(hero) {
  return `
    ${statRow('Strength', hero.strength)}
    ${statRow('Speed', hero.speed)}
    ${statRow('Intelligence', hero.speed)}`;
}

// The card of one hero on the ranking board
function boardCard(entry, index) {
  const hero = entry.hero;
  return `
    <article class="card ${entry.you ? 'you' : ''}" style="--i:${index}">
      <div class="rank">#${entry.rank}</div>
      ${avatarSvg(hero, 72)}
      <div class="card-main">
        <h3>${escapeHtml(hero.name)}
          ${entry.you ? '<span class="badge badge-you">YOU</span>' : ''}
          ${hero.real ? '<span class="badge badge-real">Real-life hero</span>' : ''}
        </h3>
        <p class="card-title">${escapeHtml(hero.title)}</p>
        <p class="card-bio">${escapeHtml(hero.bio)}</p>
      </div>
      <div class="card-stats">${statRows(hero)}</div>
      <div class="power"><span class="power-num">${hero.power}</span><span class="power-label">power</span></div>
    </article>`;
}

function setActive(id) {
  document.querySelectorAll('nav a').forEach(a => a.classList.remove('active'));
  const link = document.getElementById(id);
  if (link) link.classList.add('active');
}

// ---------- screen 1: welcome ----------

function showIntro() {
  setActive('nav-quiz-2');
  app.innerHTML = `
    <section class="panel intro">
      <p class="eyebrow">5 questions, 1 minute</p>
      <h1>Which superhero are you?</h1>
      <p class="lead">Answer a few questions. We turn your answers into a hero, work out your power,
        and find your place on the ranking board.</p>
      <label for="player-name">Your hero name</label>
      <input id="player-name" maxlength="20" placeholder="Type a name, or leave it empty" value="${escapeHtml(playerName)}">
      <button class="btn" id="start">Start the quiz</button>
    </section>`;
  document.getElementById('start').addEventListener('click', () => {
    playerName = document.getElementById('player-name').value;
    current = 0;
    choices = [];
    showQuestion();
  });
}

// ---------- screen 2: one question at a time ----------

function showQuestion() {
  const question = questions[current];
  const percent = (current / questions.length) * 100;
  app.innerHTML = `
    <section class="panel">
      <div class="progress"><div class="progress-fill" style="width:${percent}%"></div></div>
      <p class="eyebrow">Question ${current + 1} of ${questions.length}</p>
      <h2>${escapeHtml(question.text)}</h2>
      <div class="answers">
        ${question.answers.map((answer, i) => `
          <button class="answer" data-index="${i}">${escapeHtml(answer.text)}</button>`).join('')}
      </div>
    </section>`;

  document.querySelectorAll('.answer').forEach(button => {
    button.addEventListener('click', () => {
      choices.push(Number(button.dataset.index));
      current++;
      if (current < questions.length) {
        showQuestion();
      } else {
        sendAnswers();
      }
    });
  });
}

// ---------- screen 3: the result ----------

async function sendAnswers() {
  app.innerHTML = '<section class="panel"><p class="lead">Calculating your power...</p></section>';
  try {
    const response = await fetch('/api/quiz', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name: playerName, answers: choices })
    });
    if (!response.ok) {
      throw new Error('The server answered with status ' + response.status);
    }
    showResult(await response.json());
  } catch (error) {
    app.innerHTML = `<section class="panel"><h2>Something went wrong</h2>
      <p class="lead">${escapeHtml(error.message)}</p>
      <button class="btn" id="retry">Try again</button></section>`;
    document.getElementById('retry').addEventListener('click', showIntro);
  }
}

function showResult(result) {
  setActive('nav-quiz-2');
  const you = result.you;
  const closest = result.closest;
  app.innerHTML = `
    <section class="panel result">
      <p class="eyebrow">Your result</p>
      <h1>You are most like <span class="accent">${escapeHtml(closest.name)}</span></h1>
      <div class="result-grid">
        <div class="result-you">
          ${avatarSvg(you, 150)}
          <h2>${escapeHtml(you.name)}</h2>
          <p class="card-title">${escapeHtml(you.title)}</p>
          <div class="result-stats">${statRows(you)}</div>
        </div>
        <div class="result-rank">
          <div class="big-rank">#${result.rank}</div>
          <p>out of ${result.total} heroes</p>
          <p class="power-line">Power <strong>${you.power}</strong></p>
        </div>
        <div class="result-closest">
          ${avatarSvg(closest, 110)}
          <h3>${escapeHtml(closest.name)}</h3>
          <p class="card-title">${escapeHtml(closest.title)}</p>
          <p class="card-bio">${escapeHtml(closest.bio)}</p>
          <p class="power-line">Power <strong>${closest.power}</strong></p>
        </div>
      </div>
      <div class="actions">
        <button class="btn" id="to-board">See the full ranking board</button>
        <button class="btn btn-ghost" id="again">Take the quiz again</button>
      </div>
    </section>`;
  document.getElementById('to-board').addEventListener('click', () => showBoard(result.ranking));
  document.getElementById('again').addEventListener('click', showIntro);
}

// ---------- screen 4: the ranking board ----------

async function showBoard(ranking) {
  setActive('nav-board');
  if (!ranking) {
    const response = await fetch('/api/heroes');
    ranking = await response.json();
  }
  app.innerHTML = `
    <section class="board">
      <p class="eyebrow">Strongest first</p>
      <h1>Ranking board</h1>
      <p class="lead">Power = Strength + Speed + Intelligence</p>
      <div class="cards">${ranking.map(boardCard).join('')}</div>
    </section>`;
}

// ---------- start ----------

async function start() {
  const response = await fetch('/api/questions');
  questions = await response.json();
  showIntro();
}

document.getElementById('nav-quiz').addEventListener('click', e => { e.preventDefault(); showIntro(); });
document.getElementById('nav-quiz-2').addEventListener('click', e => { e.preventDefault(); showIntro(); });
document.getElementById('nav-board').addEventListener('click', e => { e.preventDefault(); showBoard(); });

start();
