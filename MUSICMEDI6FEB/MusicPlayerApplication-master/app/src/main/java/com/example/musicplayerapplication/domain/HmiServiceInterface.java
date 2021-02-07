package com.example.musicplayerapplication.domain;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.musicplayerapplication.Constants;
import com.example.musicplayerservice.IServiceListener;
import com.example.musicplayerservice.ISongTabInterface;

public class HmiServiceInterface {
    ISongTabInterface mIServiceInterface;
    HmiServiceListener mHmiServiceListener;
    IServiceListener mIServiceListener = new IServiceListener.Stub() {
        @Override
        public void notifyPlayStatus(boolean playStatus) throws RemoteException {
            if (mHmiServiceListener != null) {
                mHmiServiceListener.notifyPlayStatus(playStatus);
            }
        }
    };

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mIServiceInterface = ISongTabInterface.Stub.asInterface(iBinder);
            try {
                mIServiceInterface.registerListener(mIServiceListener);
                Log.d("Hmi", "onServiceConnected");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.d("Hmi", "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    public void play() {
        try {
            if (mIServiceInterface != null) {
                Log.d("Hmi", "play");
                mIServiceInterface.play();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        try {
            if (mIServiceInterface != null) {
                Log.d("Hmi", "pause");
                mIServiceInterface.pause();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean getCurrentPlayStatus() {
        boolean currentPlayStatus = false;
        try {
            if (mIServiceInterface != null) {
                currentPlayStatus = mIServiceInterface.getCurrentPlayStatus();
                Log.d("Hmi", "currentPlayStatus");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d("Hmi", "currentPlayStatus");
        return currentPlayStatus;
    }


    public HmiServiceInterface(Context context, HmiServiceListener hmiServiceListener) {
        mHmiServiceListener = hmiServiceListener;
        bindServiceApp(context);
    }

    private void bindServiceApp(Context context) {
        Log.d("Hmi", "bind");
        Intent serviceIntent = new Intent();
        serviceIntent.setPackage(Constants.SERVICE_PACKAGE);
        serviceIntent.setAction(Constants.SERVICE_ACTION);
        context.bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d("Hmi", "bind complete");
    }
}
