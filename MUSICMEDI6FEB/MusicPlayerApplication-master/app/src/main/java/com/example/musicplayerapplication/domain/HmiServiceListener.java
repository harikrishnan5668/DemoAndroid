package com.example.musicplayerapplication.domain;

import com.example.serviceinterface.MusicFiles;

public interface HmiServiceListener {
    void notifyPlayStatus(boolean playStatus);
    void notifyCurrentMetadata(MusicFiles file);
}
