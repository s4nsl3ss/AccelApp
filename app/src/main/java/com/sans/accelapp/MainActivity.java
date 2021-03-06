package com.sans.accelapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;

// Source: http://examples.javacodegeeks.com/android/core/hardware/sensor/android-accelerometer-example/
public class MainActivity extends Activity implements SensorEventListener {

    private float lastX, lastY, lastZ;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    //Mtask ist eine runnable Class, von uns erstellt, liegt im selben package,
    //Die Klasse lässt sich leicht mit einem Timer verbinden. (wie oft will man auslesen/Sekunde)
    //verbunden wird sie in der Methode onResume()
    private Mtask task;
    //Timer bestimmt wie oft pro Sekunde wir die Daten einlesen.
    //Derweil wird nur die X-Achse eingelesen. Als Test sozusagen.
    private Timer timer;

    private float vibrateThreshold = 0;

    private TextView currentX, currentY, currentZ, acurrentX, acurrentY, acurrentZ, maxX, maxY, maxZ;

    public Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        Context context = getApplicationContext();
            sensorManager = (SensorManager) getSystemService(context.SENSOR_SERVICE);
            if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                // success! we have an accelerometer

                accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                vibrateThreshold = accelerometer.getMaximumRange() / 2;
            } else {
                // fail we dont have an accelerometer!
            }

            //initialize vibration
            v = (Vibrator) this.getSystemService(context.VIBRATOR_SERVICE);

    }

    public void initializeViews() {
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);

        acurrentX = (TextView) findViewById(R.id.acurrentX);
        acurrentY = (TextView) findViewById(R.id.acurrentY);
        acurrentZ = (TextView) findViewById(R.id.acurrentZ);

        maxX = (TextView) findViewById(R.id.maxX);
        maxY = (TextView) findViewById(R.id.maxY);
        maxZ = (TextView) findViewById(R.id.maxZ);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            //Inputfile wird angezeigt, wenn man 2 player mode klickt
            case R.id.action_2playersmode:
                String str = "";
                try {
                    //Hier wird das File eingelesen, das wir in (Mtask) task produziert haben
                    FileInputStream fin = openFileInput("accscansX.csv");
                    int c;
                    String temp = "";
                    while ((c = fin.read()) != -1) {
                        str += Character.toString((char) c);
                    }
                } catch (Exception e) {
                   Log.e("InputStream failed", e.toString());
                }
                //Dialogfenster dienst zur Ausgabe des Inputfiles
                final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Acceleration Dates X");
                dialog.setMessage(str);
                dialog.setNeutralButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog a1 = dialog.create();
                a1.show();
              return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        timer= new Timer();
        task = new Mtask(this);
        timer.scheduleAtFixedRate(task,0,20);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if(task!=null) {
            task.cancel();
            task=null;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // clean current values
        // displayCleanValues();
        // display the current x,y,z accelerometer values
        displayCurrentValues();
        // display the max x,y,z accelerometer values
        displayMaxValues();

        acurrentX.setText(event.values[0] + "");
        acurrentY.setText(event.values[1] + "");
        acurrentZ.setText(event.values[2] + "");

        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

        // if the change is below 2, it is just plain noise
        if (deltaX < 2)
            deltaX = 0;
        if (deltaY < 2)
            deltaY = 0;
        if (deltaZ < 2)
            deltaZ = 0;

        // set the last know values of x,y,z
        lastX = event.values[0];
        lastY = event.values[1];
        lastZ = event.values[2];

        vibrate();
    }

    // if the change in the accelerometer value is big enough, then vibrate!
    // our threshold is MaxValue/2
    public void vibrate() {
        try {
            if ((deltaX > vibrateThreshold) || (deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold)) {
                v.vibrate(50);
            }
        }catch(Exception e){
            Log.e("Vibrate error ", e.toString());
        }
    }

    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        if(task!=null) {
            task.x_pos = deltaX;
        }
        currentX.setText(Float.toString(deltaX));
        currentY.setText(Float.toString(deltaY));
        currentZ.setText(Float.toString(deltaZ));
    }

    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
        if (deltaX > deltaXMax) {
            deltaXMax = deltaX;
            maxX.setText(Float.toString(deltaXMax));
        }
        if (deltaY > deltaYMax) {
            deltaYMax = deltaY;
            maxY.setText(Float.toString(deltaYMax));
        }
        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ;
            maxZ.setText(Float.toString(deltaZMax));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}




