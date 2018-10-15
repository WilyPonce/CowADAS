package com.adans.app_10;

import java.util.Calendar;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Date;

import com.adans.app_10.Cowtech54.SensorsService;

public class PruebaAct extends AppCompatActivity implements LocationListener,GpsStatus.Listener{

    static final String TAG = PruebaAct.class.getSimpleName();

    //Delay en sensores y Handler
    public Double dly = 0.1;

    //Var Boolean estado del GPS
    boolean EDOGPSBoo = false;

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
    SensorsService sensorserv;
    //Tiempo
    Date currentTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);
        EDOGPSBoo = false;

        currentTime = Calendar.getInstance().getTime();

        //InInstancia de GPSApli
        gpsapp = new GpsDataService();
        //sensorserv=new SensorsService();

        //Nombre del ususario
        String Nombre;

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








        //Recibe nombre Bundle
        Bundle XBundle = PruebaAct.this.getIntent().getExtras();

        if (XBundle != null) {
            Nombre = XBundle.getString("nomb");
        }

        /*
        //User Permission [Write SD]
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        }*/
/*
        btnMostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mostrarCursos = new Intent(getApplicationContext(), Cursos.class);
                startActivity(mostrarCursos);
            }
        });*/

        startGPSService();
        //starBinder();
        getLocation();

    }

    private void startGPSService() {
       // Intent serintent= new Intent(getApplicationContext(),SensorsService.class);
       // getApplicationContext().startService(serintent);
        Intent gpsintent=new Intent(getApplicationContext(),GpsDataService.class);
        getApplicationContext().startService(gpsintent);
    }

    private void startBinder() {
        Intent intent = new Intent(getApplicationContext(),SensorsService.class);
        getApplicationContext().bindService(intent, snsServerConn, Context.BIND_AUTO_CREATE);
        Intent sintent = new Intent(getApplicationContext(),GpsDataService.class);
        getApplicationContext().bindService(sintent,gServerConn, Context.BIND_AUTO_CREATE);
    }

    private void stopGPSService() {
        Intent serintent= new Intent(getApplicationContext(),GpsDataService.class);
        getApplicationContext().stopService(serintent);
        Intent sensintent=new Intent(getApplicationContext(),SensorsService.class);
        getApplicationContext().stopService(sensintent);
        if(sBound) {
            getApplicationContext().unbindService(snsServerConn);
            getApplicationContext().unbindService(gServerConn);
            sBound=false;};}

    protected ServiceConnection snsServerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SensorsService.SensorsBinder binder = (SensorsService.SensorsBinder) service;
            sensorserv = binder.getService();
            sBound = true;
            Log.d(TAG, "onSensorsServiceConnected");}
        @Override
        public void onServiceDisconnected(ComponentName name) {
            sBound = false;
            Log.d(TAG, "onSensorsServiceDisconnected"); }};

    protected ServiceConnection gServerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            GpsDataService.GPSBinder gpsbinder = (GpsDataService.GPSBinder) service;
            gpsapp = gpsbinder.getService();
            sBound = true;
            Log.d(TAG, "onGPSServiceConnected");}
        @Override
        public void onServiceDisconnected(ComponentName name) {
            sBound = false;
            Log.d(TAG, "onGPSServiceDisconnected"); }};

    //////
    void getLocation() {

        try {
            // Retrieve a list of location providers that have fine accuracy, no monetary cost, etc
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setCostAllowed(false);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            String providerName = locationManager.getBestProvider(criteria, true);
            Log.d("PruebAct", "Provider by (network/gps): " + providerName);

            // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) (dly * 1000), 1, this);
            locationManager.requestLocationUpdates(providerName, (long) (dly * 1000), 1, this);

            //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, (long) (0.1 * 1000), 1, this);

        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }
    ///////

    @Override
    public void onLocationChanged(Location location) {

        LAT = (float) location.getLatitude();
        LON = (float) location.getLongitude();

        textLat.setText(String.valueOf(LAT));
        textLon.setText(String.valueOf(LON));

        LocAltitude=gpsapp.getNoSats();

        Speed= location.getSpeed();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                // TODO: get here the status of the GPS, and save into a GpsStatus to be used for satellites visualization;
                // Use GpsStatus getGpsStatus (GpsStatus status)
                // https://developer.android.com/reference/android/location/LocationManager.html#getGpsStatus(android.location.GpsStatus)
                break;
        }
    }

}


