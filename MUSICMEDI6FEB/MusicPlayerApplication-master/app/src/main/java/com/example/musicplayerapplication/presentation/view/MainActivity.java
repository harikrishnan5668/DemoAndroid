package com.example.musicplayerapplication.presentation.view;

import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.musicplayerapplication.R;
import com.example.musicplayerservice.ISongTabInterface;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static  int SPLASH_SCREEN=5000; //5 seconds
    Animation fadeIn;
    ImageView image;
    TextView tv;
    protected ISongTabInterface songTabInterface;
    ServiceConnection songTabServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//hide the status bar and get a full screen view
        setContentView(R.layout.activity_main);

        fadeIn= AnimationUtils.loadAnimation(this,R.anim.fade_in);
        image=(ImageView)findViewById(R.id.imageView);
        tv=(TextView)findViewById(R.id.textView);
        image.setAnimation(fadeIn);
        tv.setAnimation(fadeIn);

        new Handler().postDelayed(new Runnable() {// run a thread after 5 seconds to start the home screen
            @Override
            public void run() {
                Intent intent=new Intent(MainActivity.this, TwoTabActivity.class);
                startActivity(intent);
                finish(); // make sure we close the splash screen so the user won't come back when it presses back key
            }
        },SPLASH_SCREEN);// time in milliseconds (1 second = 1000 milliseconds) until the run() method will be called
    }
}