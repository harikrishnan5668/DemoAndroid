// ISongTabInterface.aidl
package com.example.musicplayerservice;

// Declare any non-default types here with import statements
import com.example.musicplayerservice.IServiceListener;

interface ISongTabInterface {

  /*int play(int num);
    int pause(int num);*/

     void play();
     void pause();
     void registerListener(IServiceListener listener);
     boolean getCurrentPlayStatus();
}