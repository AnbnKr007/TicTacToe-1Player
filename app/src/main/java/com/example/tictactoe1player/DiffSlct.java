package com.example.tictactoe1player;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DiffSlct extends AppCompatActivity {
    SeekBar seekBar;
    TextView diffLabel;
    Button enterBtn;
    String[] labels = {"Easy 😍", "Medium 😏", "Impossible 😴"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diff_slct);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        seekBar   = findViewById(R.id.seekBar);
        diffLabel = findViewById(R.id.diffLabel);
        enterBtn  = findViewById(R.id.enterBtn);
        diffLabel.setText(labels[seekBar.getProgress()]);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                diffLabel.setText(labels[progress]);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        enterBtn.setOnClickListener(v -> {
            Intent intent = new Intent(DiffSlct.this, MainActivity.class);
            // 0 = Easy, 1 = Medium, 2 = Impossible
            intent.putExtra("difficulty", seekBar.getProgress());
            startActivity(intent);
        });
    }
}