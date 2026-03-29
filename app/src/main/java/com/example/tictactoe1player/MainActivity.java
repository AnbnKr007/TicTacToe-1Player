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

import com.example.tictactoe1player.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // 0 = X (human), 1 = O (AI), 2 = Empty
    int activePlayer = 0;
    int[] gameState = {2, 2, 2, 2, 2, 2, 2, 2, 2};
    int[][] winPos = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};
    String winner = "";
    ImageView[] cell = new ImageView[9];
    ImageView home, redo;
    TextView endmsg;
    int lastcell = -1;
    int wpos;
    int difficulty; // 0=Easy, 1=Medium, 2=Impossible
    boolean gameOver = false;

    // ─── Win / Draw checks (same as your original) ───────────────────────────

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

    // ─── UI: show winner / draw (same as your original) ─────────────────────

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

    // ─── Human tap (same flow as your original Tap) ──────────────────────────

    public void Tap(View view) {
        if (gameOver || activePlayer != 0) return;

        ImageView img = (ImageView) view;
        int tappedPos = Integer.parseInt(img.getTag().toString().substring(1));
        if (gameState[tappedPos] != 2) return;

        lastHumanCell = tappedPos;   // ← track human cell
        placeSymbol(tappedPos, 0);
        activePlayer = 1;

        if (checkWin()) { winner = "X"; win(); return; }
        if (checkDraw()) { draw(); return; }

        setBoardClickable(false);
        new Handler().postDelayed(this::aiMove, 500);
    }

    private void aiMove() {
        if (gameOver) return;

        int chosenPos;
        switch (difficulty) {
            case 0:  chosenPos = easyMove();  break;
            case 1:  chosenPos = mediumMove(); break;
            default: chosenPos = hardMove();   break;
        }

        lastAiCell = chosenPos;      // ← track AI cell
        placeSymbol(chosenPos, 1);
        activePlayer = 0;

        if (checkWin()) { winner = "O"; win(); return; }
        if (checkDraw()) { draw(); return; }

        setBoardClickable(true);
        redo.setClickable(true);     // ← enable redo only after AI has moved
    }

    // ─── Easy: pick any random empty cell ────────────────────────────────────

    private int easyMove() {
        List<Integer> empty = getEmptyCells();
        return empty.get(new Random().nextInt(empty.size()));
    }

    // ─── Medium: win if possible, block if needed, else random ───────────────

    private int mediumMove() {
        // 1. Can AI win right now?
        int win = findWinningMove(1);
        if (win != -1) return win;

        // 2. Can human win next turn? Block it.
        int block = findWinningMove(0);
        if (block != -1) return block;

        // 3. Otherwise random
        return easyMove();
    }

    // Finds a move that completes a line for the given player, or -1
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

    // ─── Hard: Minimax (unbeatable) ───────────────────────────────────────────

    private int hardMove() {
        int bestScore = Integer.MIN_VALUE;
        int bestPos = -1;
        for (int i = 0; i < 9; i++) {
            if (gameState[i] == 2) {
                gameState[i] = 1; // AI tries this cell
                int score = minimax(gameState, false);
                gameState[i] = 2; // undo
                if (score > bestScore) {
                    bestScore = score;
                    bestPos = i;
                }
            }
        }
        return bestPos;
    }

    // Minimax: true = AI's turn (maximising), false = human's turn (minimising)
    private int minimax(int[] board, boolean isMaximising) {
        // Terminal state checks
        for (int[] pos : winPos) {
            if (board[pos[0]] == board[pos[1]] && board[pos[1]] == board[pos[2]] && board[pos[0]] != 2) {
                return board[pos[0]] == 1 ? 10 : -10; // AI win=+10, human win=-10
            }
        }
        boolean full = true;
        for (int v : board) if (v == 2) { full = false; break; }
        if (full) return 0; // draw

        if (isMaximising) {
            int best = Integer.MIN_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i] == 2) {
                    board[i] = 1;
                    best = Math.max(best, minimax(board, false));
                    board[i] = 2;
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i] == 2) {
                    board[i] = 0;
                    best = Math.min(best, minimax(board, true));
                    board[i] = 2;
                }
            }
            return best;
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private List<Integer> getEmptyCells() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 9; i++) if (gameState[i] == 2) list.add(i);
        return list;
    }

    private void setBoardClickable(boolean clickable) {
        for (ImageView img : cell) {
            // Only re-enable cells that are still empty
            if (gameState[Integer.parseInt(img.getTag().toString().substring(1))] == 2) {
                img.setClickable(clickable);
            }
        }
    }

    // ─── Redo: undo last human move (same as your original) ──────────────────

    int lastHumanCell = -1;
    int lastAiCell = -1;

    public void redofun(View view) {
        // Undo AI's move
        if (lastAiCell != -1) {
            cell[lastAiCell].setImageResource(0);
            cell[lastAiCell].setClickable(true);
            gameState[lastAiCell] = 2;
            lastAiCell = -1;
        }
        // Undo human's move
        if (lastHumanCell != -1) {
            cell[lastHumanCell].setImageResource(0);
            cell[lastHumanCell].setClickable(true);
            gameState[lastHumanCell] = 2;
            lastHumanCell = -1;
        }
        activePlayer = 0;
        redo.setClickable(false);
    }

    // ─── Home / reset (same as your original) ────────────────────────────────

    public void homefun(View view) {
        for (ImageView img : cell) {
            img.setImageResource(0);
            img.setClickable(true);
            img.setVisibility(View.VISIBLE);
        }
        endmsg.setVisibility(View.INVISIBLE);
        gameState = new int[]{2,2,2,2,2,2,2,2,2};
        activePlayer = 0;
        gameOver = false;
        lastHumanCell = -1;
        lastAiCell = -1;
        redo.setClickable(false);
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

        difficulty = getIntent().getIntExtra("difficulty", 0);

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
    }
}