package com.example.musicplayerapplication.presentation.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayerapplication.Constants;
import com.example.musicplayerapplication.R;
import com.example.musicplayerapplication.presentation.view.TwoTabActivity;
import com.example.musicplayerservice.ISongTabInterface;

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

        initConnection();
    }

    void initConnection() {
        songTabServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // TODO Auto-generated method stub
                songTabInterface = null;
                Toast.makeText(getApplicationContext(),"Song Tab Service Disconnected", Toast.LENGTH_SHORT).show();
                Log.d("IRemote", "Binding -Song Tab Service disconnected");
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                // TODO Auto-generated method stub


                songTabInterface = ISongTabInterface.Stub.asInterface((IBinder) service);
                Toast.makeText(getApplicationContext(), "Music Player Service Connected", Toast.LENGTH_SHORT).show();
                Log.d("IRemote", "Binding is done -Song Tab Service connected");
            }
        };
        if (songTabInterface == null) {

            Intent intent = new Intent();
            intent.setPackage("com.example.musicplayerservice");
            intent.setAction("service.SongTab");
            // binding to remote service
            bindService(intent, songTabServiceConnection, Service.BIND_AUTO_CREATE);
        }
    }
    public void onDestroy() {
        super.onDestroy();
        unbindService(songTabServiceConnection);
    }

    private void bindServiceApp(Context context) {
        Log.d("Hmi", "bind");
        Intent serviceIntent = new Intent();
        serviceIntent.setPackage(Constants.SERVICE_PACKAGE);
        serviceIntent.setAction(Constants.SERVICE_PACKAGE_NAME);
        context.bindService(serviceIntent, songTabServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d("Hmi", "bind complete");

    }

}