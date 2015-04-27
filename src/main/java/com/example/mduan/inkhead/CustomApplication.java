package com.example.mduan.inkhead;

import android.app.Application;
import android.content.res.Configuration;

import com.parse.ParseObject;

/**
 * Created by Abhinav on 4/26/2015.
 */
public class CustomApplication extends Application {
    public static ParseObject currentObject;

    public CustomApplication(){
        super();
        currentObject = null;
    }

    public static void setCurrentObject(ParseObject p){
        currentObject = p;
    }

    public ParseObject getCurrentObject(){ return currentObject; }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
