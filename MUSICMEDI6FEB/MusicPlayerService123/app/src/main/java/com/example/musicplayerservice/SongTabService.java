package com.example.musicplayerservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.example.musicplayerservice.data.ServiceManager;


public class SongTabService extends Service {

   // Binder mBinder;
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ServiceManager.getServiceManager().init(getApplicationContext());
        return START_STICKY;
    }

    /**
     * IAdd definition is below
     */
    private final ISongTabInterface.Stub mBinder = new ISongTabInterface.Stub() {

        @Override
        public void play() throws RemoteException {
            ServiceManager.getServiceManager().play();
        }

        @Override
        public void pause() throws RemoteException {
            ServiceManager.getServiceManager().pause();
        }

        @Override
        public void registerListener(IServiceListener listener) throws RemoteException {
            ServiceManager.getServiceManager().setListener(listener);
        }

        @Override
        public boolean getCurrentPlayStatus() throws RemoteException {
            return ServiceManager.getServiceManager().getCurrentPlayStatus();
        }
    };
}