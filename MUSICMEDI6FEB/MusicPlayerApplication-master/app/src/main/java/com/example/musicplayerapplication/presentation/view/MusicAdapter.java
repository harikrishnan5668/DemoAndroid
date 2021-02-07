package com.example.musicplayerapplication.presentation.view;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.musicplayerapplication.R;
import com.example.serviceinterface.MusicFiles;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder>{
    private Context mContext;
    private  ArrayList<MusicFiles> mFiles;
    private FragmentManager fragmentManager;
    SongListFragment songListFragment=new SongListFragment();
    ImageView play;

    MusicAdapter (Context mContext,ArrayList<MusicFiles> mFiles)
    {
        this.mFiles=mFiles;
        this.mContext=mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.music_items,parent,false);

        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.file_name.setText(mFiles.get(position).getTitle());
        byte[] image=getAlbumArt(mFiles.get(position).getPath());
        if(image!=null)
        {
            Glide.with(mContext).asBitmap().load(image).into(holder.cover);
        }
        else
        {
            Glide.with(mContext).asBitmap().load(R.drawable.preview).into(holder.cover);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putInt("position",position);
                NowPlayingFragment nowPlayingFragment=new NowPlayingFragment();
                nowPlayingFragment.setArguments(bundle);

                /*FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.audio_items, new NowPlayingFragment());
                ft.commit();*/

                Intent intent=new Intent(mContext,TwoTabActivity.class);
                intent.putExtra("position",position);
                mContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder
    {
        TextView file_name;
        ImageView cover,play;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name=itemView.findViewById(R.id.file_name);
            cover=itemView.findViewById(R.id.music_cover);
            play=itemView.findViewById(R.id.play_pause);


        }
    }
    private  byte[] getAlbumArt(String uri)
    {
        MediaMetadataRetriever retriever= new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art=retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}
