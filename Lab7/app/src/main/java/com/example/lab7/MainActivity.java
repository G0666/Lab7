package com.example.lab7;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private int progressRabbit = 0;
    private int progressTurtle = 0;
    private Button btn_start;
    private SeekBar sb_rabbit;
    private SeekBar sb_turtle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_start = findViewById(R.id.btn_start);
        sb_rabbit = findViewById(R.id.sb_rabbit);
        sb_turtle = findViewById(R.id.sb_turtle);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_start.setEnabled(false);
                progressRabbit = 0;
                progressTurtle = 0;
                sb_rabbit.setProgress(0);
                sb_turtle.setProgress(0);
                runRabbit();
                runTurtle();
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 1)
                sb_rabbit.setProgress(progressRabbit);

            if (progressRabbit >= 100 && progressTurtle < 100) {
                showToast("兔子勝利");
                btn_start.setEnabled(true);
            }
        }
    };

    private void runRabbit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean[] sleepProbability = {true, true, false};
                while (progressRabbit < 100 && progressTurtle < 100) {
                    try {
                        Thread.sleep(100);
                        if (sleepProbability[(int) (Math.random() * 3)])
                            Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // 在 UI 線程上更新 SeekBar 和 progressRabbit
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressRabbit += 3;
                            sb_rabbit.setProgress(progressRabbit);
                        }
                    });

                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    private void runTurtle() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressTurtle < 100 && progressRabbit < 100) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace(); // 印出異常堆疊
                    }

                    // 在 UI 線程上更新 SeekBar 和 progressTurtle
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressTurtle += 1;
                            sb_turtle.setProgress(progressTurtle);
                        }
                    });

                    Message msg = new Message();
                    msg.what = 2;
                    handler.sendMessage(msg);
                }

                // 檢查烏龜是否勝利
                if (progressTurtle >= 100 && progressRabbit < 100) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast("烏龜勝利");
                            btn_start.setEnabled(true);
                        }
                    });
                }
            }
        }).start();
    }
}
