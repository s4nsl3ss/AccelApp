package com.sans.accelapp;

import android.app.Activity;
import android.util.Log;

import java.io.FileOutputStream;
import java.util.TimerTask;

/**
 * Created by sans on 01.11.2014.
 */
public class Mtask extends TimerTask {

    private int countwrites = 0;
    private String x_fullstr;
    protected float x_pos;
    private Activity a;

    public Mtask(MainActivity a){
        this.a=a;
    }

    public void run(){
        try {
            if(countwrites>=100){
                Log.i("counter done ", countwrites + "");
                countwrites=0;
            FileOutputStream output = a.openFileOutput("accscansX.csv", 0);
            output.write(x_fullstr.getBytes());
            output.close();
            }
            x_fullstr += Float.toString(x_pos);
            ++countwrites;
        } catch(Exception e){
            Log.e("Outputstream error ", e.toString());
        }

    }


}