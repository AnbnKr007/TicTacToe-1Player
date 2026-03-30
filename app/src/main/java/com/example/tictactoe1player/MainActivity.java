package com.example.tictactoe1player;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    int activePlayer;
    int humanPlayer;  // symbol chosen by human (0=X, 1=O)
    int aiPlayer;     // opposite of humanPlayer

    int[] gameState = {2, 2, 2, 2, 2, 2, 2, 2, 2};
    int[][] winPos = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};
    String winner = "";
    ImageView[] cell = new ImageView[9];
    ImageView home, redo;
    TextView endmsg;
    int lastHumanCell = -1;
    int lastAiCell = -1;
    int wpos;
    int difficulty;
    boolean gameOver = false;

    // ─── Win / Draw checks ────────────────────────────────────────────────────

    public boolean checkWin() {
        wpos = 0;
        for (int[] pos : winPos) {
            if (gameState[pos[0]] == gameState[pos[1]]
                    && gameState[pos[1]] == gameState[pos[2]]
                    && gameState[pos[0]] != 2) {
                return true;
            }
            wpos++;
        }
        return false;
    }

    public boolean checkDraw() {
        for (int a : gameState) {
            if (a == 2) return false;
        }
        return true;
    }

    // ─── UI: show winner / draw ───────────────────────────────────────────────

    public void win() {
        gameOver = true;
        for (ImageView img : cell) img.setVisibility(View.INVISIBLE);
        for (int a : winPos[wpos]) cell[a].setVisibility(View.VISIBLE);
        endmsg.setText(winner + " Won!");
        endmsg.setVisibility(View.VISIBLE);
        redo.setClickable(false);
        winner = "";
    }

    public void draw() {
        gameOver = true;
        endmsg.setText("XO Draw!");
        endmsg.setVisibility(View.VISIBLE);
        redo.setClickable(false);
    }

    // ─── Place a symbol on the board ─────────────────────────────────────────

    private void placeSymbol(int pos, int player) {
        gameState[pos] = player;
        cell[pos].setClickable(false);
        cell[pos].setImageResource(player == 0 ? R.drawable.x : R.drawable.o);
        cell[pos].setAlpha(0f);
        cell[pos].animate().alpha(1f).setDuration(200).start();
    }

    // ─── Human tap ───────────────────────────────────────────────────────────

    public void Tap(View view) {
        if (gameOver || activePlayer != humanPlayer) return;

        ImageView img = (ImageView) view;
        int tappedPos = Integer.parseInt(img.getTag().toString().substring(1));
        if (gameState[tappedPos] != 2) return;

        lastHumanCell = tappedPos;
        placeSymbol(tappedPos, humanPlayer);
        activePlayer = aiPlayer;

        if (checkWin()) {
            winner = humanPlayer == 0 ? "X" : "O";
            win();
            return;
        }
        if (checkDraw()) { draw(); return; }

        setBoardClickable(false);
        new Handler().postDelayed(this::aiMove, 500);
    }

    // ─── AI move ─────────────────────────────────────────────────────────────

    private void aiMove() {
        if (gameOver) return;

        int chosenPos;
        switch (difficulty) {
            case 0:  chosenPos = easyMove();   break;
            case 1:  chosenPos = mediumMove(); break;
            default: chosenPos = hardMove();   break;
        }

        lastAiCell = chosenPos;
        placeSymbol(chosenPos, aiPlayer);
        activePlayer = humanPlayer;

        if (checkWin()) {
            winner = aiPlayer == 0 ? "X" : "O";
            win();
            return;
        }
        if (checkDraw()) { draw(); return; }

        setBoardClickable(true);
        redo.setClickable(true);
    }

    // ─── Easy ────────────────────────────────────────────────────────────────

    private int easyMove() {
        List<Integer> empty = getEmptyCells();
        return empty.get(new Random().nextInt(empty.size()));
    }

    // ─── Medium ──────────────────────────────────────────────────────────────

    private int mediumMove() {
        int win = findWinningMove(aiPlayer);
        if (win != -1) return win;
        int block = findWinningMove(humanPlayer);
        if (block != -1) return block;
        return easyMove();
    }

    private int findWinningMove(int player) {
        for (int[] pos : winPos) {
            int playerCount = 0, emptyIdx = -1;
            for (int p : pos) {
                if (gameState[p] == player) playerCount++;
                else if (gameState[p] == 2) emptyIdx = p;
            }
            if (playerCount == 2 && emptyIdx != -1) return emptyIdx;
        }
        return -1;
    }

    // ─── Hard (Minimax) ──────────────────────────────────────────────────────

    private int hardMove() {
        int bestScore = Integer.MIN_VALUE;
        int bestPos = -1;
        for (int i = 0; i < 9; i++) {
            if (gameState[i] == 2) {
                gameState[i] = aiPlayer;
                int score = minimax(gameState, false);
                gameState[i] = 2;
                if (score > bestScore) {
                    bestScore = score;
                    bestPos = i;
                }
            }
        }
        return bestPos;
    }

    private int minimax(int[] board, boolean isMaximising) {
        for (int[] pos : winPos) {
            if (board[pos[0]] == board[pos[1]] && board[pos[1]] == board[pos[2]] && board[pos[0]] != 2) {
                return board[pos[0]] == aiPlayer ? 10 : -10;
            }
        }
        boolean full = true;
        for (int v : board) if (v == 2) { full = false; break; }
        if (full) return 0;

        if (isMaximising) {
            int best = Integer.MIN_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i] == 2) {
                    board[i] = aiPlayer;
                    best = Math.max(best, minimax(board, false));
                    board[i] = 2;
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i] == 2) {
                    board[i] = humanPlayer;
                    best = Math.min(best, minimax(board, true));
                    board[i] = 2;
                }
            }
            return best;
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private List<Integer> getEmptyCells() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 9; i++) if (gameState[i] == 2) list.add(i);
        return list;
    }

    private void setBoardClickable(boolean clickable) {
        for (ImageView img : cell) {
            if (gameState[Integer.parseInt(img.getTag().toString().substring(1))] == 2) {
                img.setClickable(clickable);
            }
        }
    }

    // ─── Redo ────────────────────────────────────────────────────────────────

    public void redofun(View view) {
        if (lastAiCell != -1) {
            cell[lastAiCell].setImageResource(0);
            cell[lastAiCell].setClickable(true);
            gameState[lastAiCell] = 2;
            lastAiCell = -1;
        }
        if (lastHumanCell != -1) {
            cell[lastHumanCell].setImageResource(0);
            cell[lastHumanCell].setClickable(true);
            gameState[lastHumanCell] = 2;
            lastHumanCell = -1;
        }
        activePlayer = humanPlayer;
        redo.setClickable(false);
    }

    // ─── Home / Reset ─────────────────────────────────────────────────────────

    public void homefun(View view) {
        for (ImageView img : cell) {
            img.setImageResource(0);
            img.setClickable(true);
            img.setVisibility(View.VISIBLE);
        }
        endmsg.setVisibility(View.INVISIBLE);
        gameState = new int[]{2,2,2,2,2,2,2,2,2};
        gameOver = false;
        lastHumanCell = -1;
        lastAiCell = -1;
        redo.setClickable(false);

        // If AI is X, it goes first on reset too
        if (humanPlayer == 1) {
            activePlayer = aiPlayer;
            setBoardClickable(false);
            new Handler().postDelayed(this::aiMove, 500);
        } else {
            activePlayer = humanPlayer;
        }
    }

    // ─── onCreate ─────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        difficulty   = getIntent().getIntExtra("difficulty", 0);
        humanPlayer  = getIntent().getIntExtra("playerSymbol", 0);
        aiPlayer     = 1 - humanPlayer;

        endmsg = findViewById(R.id.textView3);
        endmsg.setVisibility(View.INVISIBLE);

        cell[0] = findViewById(R.id.imageView1);
        cell[1] = findViewById(R.id.imageView2);
        cell[2] = findViewById(R.id.imageView3);
        cell[3] = findViewById(R.id.imageView4);
        cell[4] = findViewById(R.id.imageView5);
        cell[5] = findViewById(R.id.imageView6);
        cell[6] = findViewById(R.id.imageView7);
        cell[7] = findViewById(R.id.imageView8);
        cell[8] = findViewById(R.id.imageView9);

        home = findViewById(R.id.homeIcon);
        redo = findViewById(R.id.redoIcon);
        redo.setClickable(false);

        // If player chose O, AI is X and goes first
        if (humanPlayer == 1) {
            activePlayer = aiPlayer;
            setBoardClickable(false);
            new Handler().postDelayed(this::aiMove, 500);
        } else {
            activePlayer = humanPlayer;
        }
    }
}