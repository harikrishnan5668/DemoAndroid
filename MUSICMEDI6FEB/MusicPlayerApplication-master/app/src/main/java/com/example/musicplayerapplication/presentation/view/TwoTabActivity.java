package com.example.musicplayerapplication.presentation.view;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.example.musicplayerapplication.R;
import com.example.serviceinterface.MusicFiles;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

public class TwoTabActivity extends AppCompatActivity {

    public static final int REQUEST_CODE=1;
    static ArrayList<MusicFiles> musicFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_tab);

        permission();
    }
    public void permission()
    {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(TwoTabActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
        }
        else
        {
            musicFiles=getAllAudio(this);
            initViewPager();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                musicFiles=getAllAudio(this);
                initViewPager();
            }
            else
            {
                ActivityCompat.requestPermissions(TwoTabActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
            }
        }
    }

    public void initViewPager(){
        ViewPager viewPager=findViewById(R.id.viewpager);
        TabLayout tabLayout=findViewById(R.id.tabs);
        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    private void setUpViewPager(ViewPager viewPager) {
        PageAdapter adapter =new PageAdapter(getSupportFragmentManager());
        adapter.addFragment(new NowPlayingFragment(),"Now Playing");
        adapter.addFragment(new SongListFragment(),"Song List");
        viewPager.setAdapter(adapter);
    }

    public static ArrayList<MusicFiles> getAllAudio(Context context)
    {
        ArrayList<MusicFiles> tempAudioList=new ArrayList<>();
        Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection={
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST

        };
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);
        {
            if(cursor!=null)
            {
                while(cursor.moveToNext())
                {
                    String album=cursor.getString(0);
                    String title=cursor.getString(1);
                    String duration=cursor.getString(2);
                    String path=cursor.getString(3);
                    String artist=cursor.getString(4);

                    MusicFiles musicFiles=new MusicFiles(path,title,artist,album,duration);
                    Log.e("Path:"+ path,"Album: "+album);
                    tempAudioList.add(musicFiles);
                }
                cursor.close();
            }
        }return tempAudioList;
    }
}
