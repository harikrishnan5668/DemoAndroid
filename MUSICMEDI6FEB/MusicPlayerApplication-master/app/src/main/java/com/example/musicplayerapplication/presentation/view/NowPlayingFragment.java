package com.example.musicplayerapplication.presentation.view;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.musicplayerapplication.R;
import com.example.musicplayerservice.ISongTabInterface;
import com.example.serviceinterface.MusicFiles;

import java.util.ArrayList;
import java.util.Random;

import static com.example.musicplayerapplication.presentation.view.TwoTabActivity.musicFiles;

public class NowPlayingFragment extends Fragment implements MediaPlayer.OnCompletionListener{

    TextView songName,artistName,albumName,elapsedTime,durationPlayed;
    ImageView shuffle,rewind,previous,next,forward,repeat,cover;
    ImageView play;
    SeekBar seekBar;
    int position=1;
    static Uri uri;
    int num=1;
    ArrayList<MusicFiles> listSongs=new ArrayList<>();
    static  boolean shuffleBoolean= false, repeatBoolean= false;
    static MediaPlayer mediaPlayer;
    private Handler handler=new Handler();
    private Thread playThread,prevThread,nextThread,rewindThread,forwardThread;
    protected ISongTabInterface songTabInterface;
    ServiceConnection songTabServiceConnection;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        if(getArguments()!=null){
            position = getArguments().getInt("position");
        }
        View view = inflater.inflate(R.layout.fragment_now_playing, container, false);
        return view;
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initConnection();
        initViews(view);
        getIntentMethod();

        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffleBoolean)
                {
                    shuffleBoolean=false;
                    shuffle.setImageResource(R.drawable.shuffle);
                }
                else
                {
                    shuffleBoolean=true;
                    shuffle.setImageResource(R.drawable.shuffle_on);

                }
            }
        });
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repeatBoolean)
                {
                    repeatBoolean=false;
                    repeat.setImageResource(R.drawable.repeat);
                }
                else
                {
                    repeatBoolean=true;
                    repeat.setImageResource(R.drawable.repeat_on);

                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(seekBar!=null && fromUser)
                {
                    mediaPlayer.seekTo(progress*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
       getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null)
                {
                    int mCurrentPos=mediaPlayer.getCurrentPosition()/1000;
                    seekBar.setProgress(mCurrentPos);
                    durationPlayed.setText(formattedTime(mCurrentPos));
                }
                handler.postDelayed(this,1000);
            }
        });
    }


    @Override
    public void onResume() {
        playThread();
        prevThread();
        nextThread();
        rewindThread();
        forwardThread();
        super.onResume();
    }

    private void forwardThread() {
        forwardThread=new Thread()
        {
            @Override
            public void run() {
                super.run();
                forward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        forwardBtnClicked();

                    }
                });
            }
        };
        forwardThread.start();
    }

    private void forwardBtnClicked() {
        position=mediaPlayer.getCurrentPosition();
        if(mediaPlayer.isPlaying())
        {
            position=position+5000;
            mediaPlayer.seekTo(position);
        }
    }

    private void rewindThread() {
        rewindThread=new Thread()
        {
            @Override
            public void run() {
                super.run();
                rewind.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rewindBtnClicked();

                    }
                });
            }
        };
        rewindThread.start();
    }

    private void rewindBtnClicked() {
        position=mediaPlayer.getCurrentPosition();
        if(mediaPlayer.isPlaying())
        {
            position=position-5000;
            mediaPlayer.seekTo(position);
        }
    }

    private void nextThread() {
        nextThread=new Thread()
        {
            @Override
            public void run() {
                super.run();
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();

                    }
                });
            }
        };
        nextThread.start();
    }

    private void nextBtnClicked() {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean)
            {
                position=getRandom(listSongs.size()-1);
            }
            else if (!shuffleBoolean && !repeatBoolean)
            {
                position=((position+1) % listSongs.size());
            }

            uri=Uri.parse(listSongs.get(position).getPath());
            mediaPlayer=MediaPlayer.create(requireContext(),uri);
            metaData(uri);
            songName.setText(listSongs.get(position).getTitle());
            artistName.setText(listSongs.get(position).getArtist());
            albumName.setText(listSongs.get(position).getAlbum());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null)
                    {
                        int mCurrentPos=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPos);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            play.setImageResource(R.drawable.pause);
            mediaPlayer.start();
        }
        else
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean)
            {
                position=getRandom(listSongs.size()-1);
            }
            else if (!shuffleBoolean && !repeatBoolean)
            {
                position=((position+1) % listSongs.size());
            }
            uri=Uri.parse(listSongs.get(position).getPath());
            mediaPlayer=MediaPlayer.create(requireContext(),uri);
            metaData(uri);
            songName.setText(listSongs.get(position).getTitle());
            artistName.setText(listSongs.get(position).getArtist());
            albumName.setText(listSongs.get(position).getAlbum());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null)
                    {
                        int mCurrentPos=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPos);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            play.setImageResource(R.drawable.play);
        }
    }

    private int getRandom(int i) {
        Random random=new Random();
        return random.nextInt(i+1);
    }

    private void prevThread() {
        prevThread=new Thread()
        {
            @Override
            public void run() {
                super.run();
                previous.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        previousBtnClicked();

                    }
                });
            }
        };
        prevThread.start();
    }

    void initConnection() {
        songTabServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // TODO Auto-generated method stub
                songTabInterface = null;
                Toast.makeText(requireActivity(),"Tab Service Disconnected", Toast.LENGTH_SHORT).show();
                Log.d("IRemote", "Binding -Song Tab Service disconnected");
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                // TODO Auto-generated method stub


                songTabInterface = ISongTabInterface.Stub.asInterface((IBinder) service);
                Toast.makeText(requireActivity(), "NowPlaying Fragment Connected", Toast.LENGTH_SHORT).show();
                Log.d("IRemote", "Binding is done -Song Tab Service connected");
            }
        };
        if (songTabInterface == null) {

            Intent intent = new Intent();
            intent.setPackage("com.example.musicplayerservice");
            intent.setAction("service.SongTab");
            // binding to remote service
            getActivity().bindService(intent, songTabServiceConnection, Service.BIND_AUTO_CREATE);
        }
    }
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(songTabServiceConnection);
    }

    private void previousBtnClicked() {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean)
            {
                position=getRandom(listSongs.size()-1);
            }
            else if (!shuffleBoolean && !repeatBoolean)
            {
                position=((position - 1)< 0 ? (listSongs.size()-1):(position-1));
            }

            uri=Uri.parse(listSongs.get(position).getPath());
            mediaPlayer=MediaPlayer.create(requireContext(),uri);
            metaData(uri);
            songName.setText(listSongs.get(position).getTitle());
            artistName.setText(listSongs.get(position).getArtist());
            albumName.setText(listSongs.get(position).getAlbum());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null)
                    {
                        int mCurrentPos=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPos);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            play.setImageResource(R.drawable.pause);
            mediaPlayer.start();
        }
        else
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean)
            {
                position=getRandom(listSongs.size()-1);
            }
            else if (!shuffleBoolean && !repeatBoolean)
            {
                position=((position - 1)< 0 ? (listSongs.size()-1):(position-1));
            }
            uri=Uri.parse(listSongs.get(position).getPath());
            mediaPlayer=MediaPlayer.create(requireContext(),uri);
            metaData(uri);
            songName.setText(listSongs.get(position).getTitle());
            artistName.setText(listSongs.get(position).getArtist());
            albumName.setText(listSongs.get(position).getAlbum());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null)
                    {
                        int mCurrentPos=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPos);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            play.setImageResource(R.drawable.play);
        }
    }

    private void playThread() {
        playThread=new Thread()
        {
            @Override
            public void run() {
                super.run();
                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseBtnClicked();

                    }
                });
            }
        };
        playThread.start();
    }


    private void playPauseBtnClicked() {
        if (mediaPlayer.isPlaying()) {
           play.setImageResource(R.drawable.play);
            try {
                songTabInterface.play();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
          /*  try {

                if (songTabInterface.pause(num)==1){
                    mediaPlayer.pause();
                    Toast.makeText(getActivity(),"pause clicked",Toast.LENGTH_SHORT).show();
                    num=1;
                }
                Log.d("IRemote", "Binding - Add operation");
            }catch (RemoteException e){
                e.printStackTrace();
            }*/
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null)
                    {
                        int mCurrentPos=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPos);
                    }
                    handler.postDelayed(this,1000);
                }
            });

        }
        else {
            play.setImageResource(R.drawable.pause);

            /*try {

                if (songTabInterface.play(num)==1){
                    mediaPlayer.start();
                    Toast.makeText(getActivity(),"play clicked",Toast.LENGTH_SHORT).show();
                    num=1;
                }
                Log.d("IRemote", "Binding - Add operation");
            }catch (RemoteException e){
                e.printStackTrace();
            }*/
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null)
                    {
                        int mCurrentPos=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPos);
                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
    }



    private String formattedTime(int mCurrentPos) {
        String totalOut=" ";
        String totalNew=" ";
        String seconds=String.valueOf(mCurrentPos % 60);
        String minutes=String.valueOf(mCurrentPos / 60);
        totalOut=minutes+":"+seconds;
        totalNew=minutes+":"+"0"+seconds;
        if(seconds.length()==1)
        {
            return totalNew;
        }
        else
        {
            return totalOut;
        }



    }

    public void initViews(View view)
    {
        songName=view.findViewById(R.id.songName);
        artistName=view.findViewById(R.id.artistName);
        albumName=view.findViewById(R.id.albumName);
        durationPlayed=view.findViewById(R.id.durationPlayed);
        elapsedTime=view.findViewById(R.id.elapsedTime);

        shuffle=view.findViewById(R.id.shuffle);
        rewind=view.findViewById(R.id.rewind);
        previous=view.findViewById(R.id.previous);
        play=view.findViewById(R.id.play);
        next=view.findViewById(R.id.next);
        forward=view.findViewById(R.id.forward);
        repeat=view.findViewById(R.id.repeat);
        cover=view.findViewById(R.id.cover);
        seekBar=view.findViewById(R.id.seekBar);
    }
    public void getIntentMethod()
    {
        position=getActivity().getIntent().getIntExtra("position",1);
        listSongs=musicFiles;
        if (listSongs!=null && listSongs.size() > position) {
            play.setImageResource(R.drawable.pause);
            uri = Uri.parse(listSongs.get(position).getPath());
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = MediaPlayer.create(requireContext(), uri);
                mediaPlayer.start();
            } else {
                mediaPlayer = MediaPlayer.create(requireContext(), uri);
                mediaPlayer.start();
            }
            mediaPlayer.setOnCompletionListener(this);
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            metaData(uri);
            songName.setText(listSongs.get(position).getTitle());
            artistName.setText(listSongs.get(position).getArtist());
            albumName.setText(listSongs.get(position).getAlbum());
        }
    }
    public  void metaData(Uri uri)
    {
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal=Integer.parseInt(listSongs.get(position).getDuration())/1000;
        elapsedTime.setText(formattedTime(durationTotal));
        byte[] art=retriever.getEmbeddedPicture();
        if(art!=null)
        {
            Glide.with(this).asBitmap().load(art).into(cover);
        }
        else
        {
            Glide.with(this).asBitmap().load(R.drawable.preview).into(cover);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        nextBtnClicked();
        if(mediaPlayer!=null)
        {
            play.setImageResource(R.drawable.pause);
            mediaPlayer=MediaPlayer.create(requireContext(),uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }

    }
}