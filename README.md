# TicTacToe-1Player 🎮

> *For those who play alone — by choice, obviously.*

A single-player Tic Tac Toe Android app where you go head-to-head against an AI opponent. Built for the lone wolves, the independent thinkers, and anyone whose friends are "busy" again.

---

## Features

- **Three difficulty levels** — pick your poison via a sleek SeekBar selector
  - 😍 **Easy** — the AI moves randomly. You will win. Feel good about it.
  - 😏 **Medium** — the AI wins if it can, blocks you if it must, otherwise wanders. A fair fight.
  - 😴 **Impossible** — powered by the **Minimax algorithm**. The AI plays perfectly. You cannot win. You can only draw, if you're good enough.

- **Smooth animations** — every move fades in with a subtle alpha animation
- **Undo button** — made a mistake? Redo undoes both your last move *and* the AI's response, putting you right back where you were
- **Instant reset** — home button wipes the board and starts fresh

---

## How It Works

### Difficulty System

| Level | Strategy |
|---|---|
| Easy | Random empty cell |
| Medium | Win if possible → Block if needed → Random |
| Impossible | Minimax (always optimal) |

### Minimax Algorithm

On **Impossible** difficulty, the AI uses the [Minimax algorithm](https://en.wikipedia.org/wiki/Minimax) — a recursive decision-tree search that evaluates every possible future game state. It assigns:
- `+10` for AI win
- `-10` for human win
- `0` for draw

The AI always picks the move with the highest score, making it genuinely unbeatable. The best you can do is force a draw.

---

## Project Structure

```
app/
├── java/com/example/tictactoe1player/
│   ├── DiffSlct.java          # Difficulty selection screen
│   └── MainActivity.java      # Game logic + AI engine
└── res/
    ├── layout/
    │   ├── activity_diff_slct.xml   # SeekBar difficulty UI
    │   └── activity_main.xml        # Game board UI
    └── drawable/
        ├── x.png
        ├── o.png
        └── grid.png
```

---

## Setup

1. Clone the repo
   ```bash
   git clone https://github.com/yourusername/TicTacToe-1Player.git
   ```
2. Open in **Android Studio**
3. Run on an emulator or physical device (API 24+)

---

## Tech Stack

- **Language** — Java
- **UI** — XML layouts with ConstraintLayout
- **Min SDK** — API 24
- **Target SDK** — API 35
- **No external dependencies** — pure Android SDK

---

## AI Behavior Notes

- The AI waits **500ms** before moving so it feels natural rather than instant
- The board is locked during the AI's turn to prevent input conflicts
- Undo is only available after the AI has responded — no half-states

---

## License

MIT License. Do whatever you want with it. You're alone anyway.
