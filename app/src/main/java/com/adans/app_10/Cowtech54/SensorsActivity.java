package com.adans.app_10.Cowtech54;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.adans.app_10.GpsDataService;
import com.adans.app_10.R;

import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class SensorsActivity extends AppCompatActivity {

    static final String TAG = SensorsActivity.class.getSimpleName();

    //tv y imageview Indicador;
    TextView textLat, textLon;
    TextView textAccX, textAccY, textAccZ;
    TextView textAngX, textAngY, textAngZ;
    TextView textGyrX, textGyrY, textGyrZ;

    LocationManager locationManager;

    //Var Velocida
    double Speed;
    double LON, LAT;
    double ALT;
    int LocAltitude;
    //No. de satelites
    int NoSats;

    //Boolean del Bind
    boolean sBound = false;
    //Instancia GPS App
    GpsDataService gpsapp;
    //Instancia Sensores Servicio
    SensorsService sensorsService;
    //Tiempo
    Date currentTime;

    //Vars observers from services
    Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Textview de los indicadores
        textLat = (TextView) findViewById(R.id.latText);
        textLon = (TextView) findViewById(R.id.lonText);
        textAccX = (TextView) findViewById(R.id.accXText);
        textAccY = (TextView) findViewById(R.id.accYText);
        textAccZ = (TextView) findViewById(R.id.accZText);
        textGyrX = (TextView) findViewById(R.id.gyrXText);
        textGyrY = (TextView) findViewById(R.id.gyrYText);
        textGyrZ = (TextView) findViewById(R.id.gyrZText);
        textAngX = (TextView) findViewById(R.id.angXText);
        textAngY = (TextView) findViewById(R.id.angYText);
        textAngZ = (TextView) findViewById(R.id.angZText);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Start SensorsBinder
        startBinder();

    }

    //UNBIND METHOD
    private void unbindSensors(){

            //getApplicationContext().unbindService(sServerConn);
            getApplicationContext().unbindService(snsServerConn);

            if(disposable!=null)
                disposable.dispose();
    }
    //Sensors Service Connection with disposable javaRX observer
    protected ServiceConnection snsServerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SensorsService.SensorsBinder snsbinder = (SensorsService.SensorsBinder) service;
            sensorsService = snsbinder.getService();
            //sensorsService.startRepeating();
            sBound = true;
            Log.d(TAG, "onSensorsServiceConnected");

            disposable = sensorsService.observeString()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(string -> textAccX.setText(string[0]));
            disposable = sensorsService.observeString()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(string -> textAccY.setText(string[1]));
            disposable = sensorsService.observeString()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(string -> textAccZ.setText(string[2]));
            disposable = sensorsService.observeString()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(string -> textGyrX.setText(string[3]));
            disposable = sensorsService.observeString()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(string -> textGyrY.setText(string[4]));
            disposable = sensorsService.observeString()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(string -> textGyrZ.setText(string[5]));
            disposable = sensorsService.observeString()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(string -> textAngX.setText(string[6]));
            disposable = sensorsService.observeString()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(string -> textAngY.setText(string[7]));
            disposable = sensorsService.observeString()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(string -> textAngZ.setText(string[8]));
            disposable = sensorsService.observeString()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(string -> textLat.setText(string[9]));
            disposable = sensorsService.observeString()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(string -> textLon.setText(string[10]));
                //11 NoSats
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            sBound = false;
            Log.d(TAG, "onSensorsServiceDisconnected"); }};


    private void startBinder() {
        Intent intent = new Intent(getApplicationContext(),SensorsService.class);
        getApplicationContext().bindService(intent, snsServerConn, Context.BIND_AUTO_CREATE);
        //Intent sintent = new Intent(getApplicationContext(),GpsDataService.class);
        //getApplicationContext().bindService(sintent,gServerConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Sensors service unbinded");

        unbindSensors();

        super.onDestroy();
        //deviceConnected = false; //make sure it is false
    }

}

