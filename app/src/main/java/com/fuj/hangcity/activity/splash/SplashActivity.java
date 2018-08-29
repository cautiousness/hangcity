package com.fuj.hangcity.activity.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuj.hangcity.R;
import com.fuj.hangcity.activity.main.MainActivity;

import cn.bmob.v3.Bmob;


/**
 * Created by fuj
 */
public class SplashActivity extends AppCompatActivity {
    private int i = 3;
    private TextView timeTV;
    private int delay = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        Bmob.initialize(this, "36b34f5774cdf7d32727edf43da003a9");

        countDown();
        imageAnim();
    }

    private void countDown() {
        timeTV = (TextView) findViewById(R.id.splash_timeTV);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (i > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timeTV.setText(getString(R.string.splash_timeTV, String.valueOf(i--)));
                        }
                    });

                    try {
                        synchronized (this) {
                            wait(delay);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).start();
    }

    private void imageAnim() {
        ImageView imageView = (ImageView) findViewById(R.id.loadImage);
        AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
        animation.setDuration(2000);
        imageView.setAnimation(animation);
    }
}
