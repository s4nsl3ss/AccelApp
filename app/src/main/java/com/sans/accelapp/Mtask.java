package com.sans.accelapp;

import android.app.Activity;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.TimerTask;

/**
 * Created by sans on 01.11.2014.
 */
public class Mtask extends TimerTask {

    //zaehlt die Durchgaenge
    private int countwrites = 0;

    //der String waechst pro run-Durchlauf um eine x-Position an
    private String x_fullstr;

    //der aktuelle Wert wird von MainActivity geschrieben
    protected float x_pos;

    //MainActivity wird im Konstruktur uebergeben
    private Activity a;

    //Konstruktor, damit wir auf openFileOutput zugreifen koennen,
    //warum man das braucht, weiss ich eigtl nicht so genau
    public Mtask(MainActivity a){
        this.a=a;
    }

    public void run(){
        try {
            //wenn eine Sekunde vergangen
            //schreiben wir unser File neu
            //und setzen die Variablen zurueck
            if(countwrites>=50){
                Log.i("counter done ", countwrites + "");
                countwrites=0;
            FileOutputStream output = a.openFileOutput("accscansX.csv", 0);
            output.flush();
            output.write(x_fullstr.getBytes());
            output.close();
            x_fullstr = "";
            }
            //bei jedem Durchlauf fuellen wir x_fullstr um eine Position
            x_fullstr += Float.toString(x_pos) + ";";
            //zaehlt die Durchgaenge
            ++countwrites;
        } catch(Exception e){
            Log.e("Outputstream error ", e.toString());
        }

    }


}