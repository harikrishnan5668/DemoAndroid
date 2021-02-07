package com.example.serviceinterface;

import android.os.Parcel;
import android.os.Parcelable;

public class MusicFiles implements Parcelable {
    private String path;
    private String title;
    private String artist;
    private String album;
    private String duration;

    public  MusicFiles() {

    }

    public MusicFiles(String path, String title, String artist, String album, String duration) {
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
    }

    protected MusicFiles(Parcel in) {
        path = in.readString();
        title = in.readString();
        artist = in.readString();
        album = in.readString();
        duration = in.readString();
    }

    public static final Creator<MusicFiles> CREATOR = new Creator<MusicFiles>() {
        @Override
        public MusicFiles createFromParcel(Parcel in) {
            return new MusicFiles(in);
        }

        @Override
        public MusicFiles[] newArray(int size) {
            return new MusicFiles[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(path);
        parcel.writeString(title);
        parcel.writeString(artist);
        parcel.writeString(album);
        parcel.writeString(duration);
    }
}
