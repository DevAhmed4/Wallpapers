package com.devMohanned.devteam.wallpaper_app.OnePiece;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.ads.MobileAds;

public class splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        //Media player Object
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.splash_screen_sound);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    //Initialise Adds to get ready to show
                    MobileAds.initialize(getApplicationContext(), Utilities.app_id_num);
                    mp.start();
                    sleep(3000);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

    }
}
