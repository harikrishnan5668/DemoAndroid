package com.example.musicplayerservice.data;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;

import com.example.musicplayerservice.IServiceListener;
import com.example.serviceinterface.MusicFiles;

import java.util.ArrayList;

public class ServiceManager {
    private final static ServiceManager serviceManager = new ServiceManager();
    private Context mContext;
    private boolean mCurrentPlayStatus = false;
    static MediaPlayer mediaPlayer;
    IServiceListener mServiceListener;
    ArrayList<MusicFiles> mAllSongfiles;
    public static ServiceManager getServiceManager() {
        return serviceManager;
    }

    private MediaPlayer.OnCompletionListener mOnCompletionListener
            = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {

        }
    };

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            mediaPlayer.start();
            try {
                if (mServiceListener != null) {
                    mServiceListener.notifyPlayStatus(true);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };
    public void init(Context context) {
        mContext = context;
        ArrayList<MusicFiles> files = getAllAudio(mContext);
        mAllSongfiles = files;
        if (files != null & files.size() > 0) {
            Log.e("PlayerService","INIT filesize:" + files.size() + "Files: " + files);
            Uri uri = Uri.parse(files.get(0).getPath());
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(mOnCompletionListener);
            mediaPlayer.setOnPreparedListener(mOnPreparedListener);
            setSongToPlayer(0);
        }

    }

    public static ArrayList<MusicFiles> getAllAudio(Context context) {
        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST

        };
        Cursor cursor = context.getContentResolver()
                .query(uri,projection,null,null, null);{
            if(cursor!=null) {
                while(cursor.moveToNext()) {
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
        }
        return tempAudioList;
    }

    public void play() {
        Log.d("Service", "playClicked");
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mCurrentPlayStatus = true;
        }
    }

    public void pause() {
        Log.d("Service", "playClicked");
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mCurrentPlayStatus = false;
        }
    }

    public boolean getCurrentPlayStatus() {
        Log.d("Service", "mCurrentPlayStatus " + mCurrentPlayStatus);
        return mCurrentPlayStatus;
    }

    public void setSongToPlayer(int position) {
        Log.d("Service", "playClicked");
        try {
            mediaPlayer.setDataSource(mAllSongfiles.get(position).getPath());
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
        play();
    }

     public void setListener(IServiceListener listener) {
         mServiceListener = listener;
     }
}
