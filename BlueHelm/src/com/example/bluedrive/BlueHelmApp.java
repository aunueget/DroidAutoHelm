package com.example.bluedrive;

import android.app.Application;

/**
 * Created with IntelliJ IDEA.
 * User: dell
 * Date: 11/17/13
 * Time: 12:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class BlueHelmApp extends Application {
    BluetoothChatService blueComm;
    @Override
    public void onCreate() {
        super.onCreate();    //To change body of overridden methods use File | Settings | File Templates.
        blueComm=null;
    }
    public void setBlueComm(BluetoothChatService tempService){
        blueComm=tempService;
    }
    public BluetoothChatService getBlueComm(){
        return blueComm;
    }
}
