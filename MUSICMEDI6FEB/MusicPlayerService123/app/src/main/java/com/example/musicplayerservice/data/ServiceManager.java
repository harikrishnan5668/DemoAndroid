package com.example.musicplayerservice.data;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import com.example.musicplayerservice.IServiceListener;
import com.example.serviceinterface.MusicFiles;
import java.util.ArrayList;

public class ServiceManager {
    private final static ServiceManager serviceManager = new ServiceManager();
    private Context mContext;
    private Thread thread;
    private boolean playStatus = false;
    static MediaPlayer mediaPlayer;
    IServiceListener mServiceListener;
    public static ServiceManager getServiceManager() {
        return serviceManager;
    }

    public void init(Context context) {
        mContext = context;
        ArrayList<MusicFiles> files = getAllAudio(mContext);
        if (files != null) {
            Log.e("PlayerService","INIT filesize:" + files.size() + "Files: " + files);
        }

    }

    public static ArrayList<MusicFiles> getAllAudio(Context context) {
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

    public void play() {
        Log.d("Service", "playClicked");
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            try {
                if (thread == null) {
                    thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                playStatus = !playStatus;
                                Thread.sleep(1000);
                                mServiceListener.notifyPlayStatus(playStatus);
                            } catch (Exception e) {
                                e.getLocalizedMessage();
                            }
                        }
                    });
                    thread.start();
                } else {
                    thread.run();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("Service", "playClicked finish");
        }
        else
        {
            mediaPlayer.start();
        }
       // public void setListener(IServiceListener listener) { mServiceListener = listener;
       /* try {
            if (thread == null) {
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            playStatus = !playStatus;
                            Thread.sleep(1000);
                            mServiceListener.notifyPlayStatus(playStatus);
                        } catch (Exception e) {
                            e.getLocalizedMessage();
                        }
                    }
                });
                thread.start();
            } else {
                thread.run();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("Service", "playClicked finish");*/

    }
}
