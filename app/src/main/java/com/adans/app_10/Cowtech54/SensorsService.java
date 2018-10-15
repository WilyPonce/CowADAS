package com.adans.app_10.Cowtech54;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.adans.app_10.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

import static com.adans.app_10.Cowtech54.Util.round;

public class SensorsService extends Service implements SensorEventListener,LocationListener,GpsStatus.Listener{

    private final String TAG = SensorsService.class.getSimpleName();

    private final IBinder cBinder = new SensorsBinder();


    float AX, AY, AZ, GX, GY, GZ, AnX, AnY, AnZ;
    String AX_s, AY_s, AZ_s, GX_s, GY_s, GZ_s, AnX_s, AnY_s, AnZ_s;

    //
    float Rot[]=null; //for gravity rotational data¿
    float I[]=null; //for magnetic rotational data
    float accels[]=new float[3];
    float mags[]=new float[3];
    float[] values = new float[3];

    float azimuth;
    float pitch;
    float roll;

    //GPS  Var Velocida
    double Speed;
    double LON = 0.0;
    double LAT = 0.0;
    int NoSats; //No. de satelites
    int LocAltitude;
    LocationManager locationManager;
    boolean isGPSavailable = false;

    //Sampling time step
    Double dly = 0.1;

    //Timestamp
    String ts;

    //Handler
    private Handler sensHandler = new Handler();

    //Files
    private MessageFiles sensorsCSV;

    //Preferences
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    //Emmitter for obsever
    private ObservableEmitter<String[]> stringObserver;
    private Observable<String[]> stringObservable;
    private String[] mySensorsEmmiter = new String[12];

    @Override
    public void onCreate (){
        super.onCreate();


        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        List<Sensor> listaSensores = sensorManager.getSensorList(Sensor.TYPE_ALL);

        listaSensores = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

        //Preferences
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();


        if (!listaSensores.isEmpty()) {
            Sensor acelerometerSensor = listaSensores.get(0);
            sensorManager.registerListener(this, acelerometerSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        listaSensores = sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE);

        if (!listaSensores.isEmpty()) {
            Sensor giroscopioSensor = listaSensores.get(0);
            sensorManager.registerListener(this, giroscopioSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        listaSensores = sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);

        if (!listaSensores.isEmpty()) {
            Sensor MFSensor = listaSensores.get(0);
            sensorManager.registerListener(this, MFSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }


        Log.d(TAG , "OnCreate sensors service");
        //Message files init creating the folders, prefixis and time generetor
        initMessageFiles(sensorsCSV);

        //START GETTING GPS LOC
        getLocation();

        startRepeating();

    }

    //RxJava Observable
    public Observable<String[]> observeString(){
        if(stringObservable== null) {
            stringObservable = Observable.create(emitter -> stringObserver = emitter);
            stringObservable = stringObservable.share();
        }
        return stringObservable;
    }

    @Override
    public void onGpsStatusChanged(int i) {

    }

    public class SensorsBinder extends Binder {
        public SensorsService getService() {return SensorsService.this;}
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Bound");
        return cBinder;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            switch (event.sensor.getType()) {

                case Sensor.TYPE_ACCELEROMETER:
                    AX = event.values[0];
                    AY = event.values[1];
                    AZ = event.values[2];

                    //Storing Values + Tab for CSV
                    AX_s= "\t" + String.valueOf(AX);
                    AY_s= "\t" + String.valueOf(AY);
                    AZ_s= "\t" + String.valueOf(AZ);

                    accels = event.values.clone();

                    break;

                case Sensor.TYPE_GYROSCOPE:
                    GX = event.values[0];
                    GY = event.values[1];
                    GZ = event.values[2];

                    GX_s= "\t" + String.valueOf(GX);
                    GY_s= "\t" + String.valueOf(GY);
                    GZ_s= "\t" + String.valueOf(GZ);


                    break;

                case Sensor.TYPE_MAGNETIC_FIELD:
                    mags = event.values.clone();
                    break;
            }
            Long tsLong = System.currentTimeMillis();
            ts = tsLong.toString();
        }
        if (mags != null && accels != null) {
            Rot = new float[9];
            I= new float[9];
            SensorManager.getRotationMatrix(Rot, I, accels, mags);
            // Correct if screen is in Landscape

            float[] outR = new float[9];
            SensorManager.remapCoordinateSystem(Rot, SensorManager.AXIS_X,SensorManager.AXIS_Z, outR);
            SensorManager.getOrientation(outR, values);

            //azimuth
            AnX = values[0] * 57.2957795f; //looks like we don't need this one
            //pitch
            AnY = values[1] * 57.2957795f;
            //roll
            AnZ = values[2] * 57.2957795f;

            AnX_s= "\t" + String.valueOf(AnX);
            AnY_s= "\t" + String.valueOf(AnY);
            AnZ_s= "\t" + String.valueOf(AnZ);

            mags = null; //retrigger the loop when things are repopulated
            accels = null; ////retrigger the loop when things are repopulated
        }

        if(stringObserver!=null){

            mySensorsEmmiter[0] = AX_s;
            mySensorsEmmiter[1] = AY_s;
            mySensorsEmmiter[2] = AZ_s;
            mySensorsEmmiter[3] = GX_s;
            mySensorsEmmiter[4] = GY_s;
            mySensorsEmmiter[5] = GZ_s;
            mySensorsEmmiter[6] = AnX_s;
            mySensorsEmmiter[7] = AnY_s;
            mySensorsEmmiter[8] = AnZ_s;
            mySensorsEmmiter[9] = String.valueOf(LAT);
            mySensorsEmmiter[10] = String.valueOf(LON);
            mySensorsEmmiter[11] = String.valueOf(NoSats);

            stringObserver.onNext(mySensorsEmmiter);
        }

    }

    public void startRepeating() {
        Log.d(TAG, "Logging sensors started");
        sensorsRunnable.run();
        Toast.makeText(getApplicationContext(), "Guardando Datos, Cada " + dly + " Segundos", (int) (dly * 1000)).show();

        /*
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = new Date();
            fecha = dateFormat.format(date);*/

    }

    private Runnable sensorsRunnable = new Runnable() {

        double i= -0.1;
        @Override
        public void run() {
            //final MantBDD mantBDD = new MantBDD(getApplicationContext());
            //NOSts=String.valueOf(gpsapp.getNoSats());
            //FC=String.valueOf(cowService.getFuleprom());

            //mantBDD.agregarCurso(TS,AX, AY, AZ, GX, GY, GZ, AGX, AGY, Speed, LAT, LOG, ALT, NOSts);
            //float AX=sensorserv.getAX(); float AY=sensorserv.getAY(); float AZ=sensorserv.getAZ();


            //
            boolean sensorsEnabled = sharedPref.getBoolean("sensor_enable_pref",true);

            boolean accEnabled = sharedPref.getBoolean("sensor_acc_pref",true);
            boolean gyrEnabled = sharedPref.getBoolean("sensor_gyr_pref",true);
            boolean angEnabled = sharedPref.getBoolean("sensor_ang_pref",true);

            if(sensorsCSV!=null && sensorsEnabled && sensorsCSV.f != null) {
                //CSV file entries
                String entries;
                entries = "" + i;
                i = round(i + 0.1,1);  //time in ms
                if(accEnabled)
                    entries = entries + AX_s + AY_s + AZ_s;
                if(gyrEnabled)
                    entries = entries + GX_s + GY_s + GZ_s;
                if(angEnabled)
                    entries = entries + AnX_s + AnY_s + AnZ_s;
                if(isGPSavailable)
                    entries = entries + "\t" + LAT + "\t" + LON;

                    sensorsCSV.writeInFile(entries);
                    // Log.d(TAG, "Writing in file");

            }


            Double dlyto = 0.1;//Segundos
            sensHandler.postDelayed(this, (long) (dlyto * 1000));
        }

    };

    public void stopRepeating() {
        sensHandler.removeCallbacks(sensorsRunnable);
        //exportDatabse("BDDSensors");
        //stopGPSService();
    }

    //Sql-Memory
    /*public void exportDatabse(String BDDSensors) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            File sdDow = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + getActivity().getPackageName() + "//databases//" + BDDSensors + "";
                String backupDBPath = getDateString() + " backupBDD" + ".db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sdDow, backupDBPath);
                Toast.makeText(getApplicationContext(), "Guardando BDD", Toast.LENGTH_SHORT).show();

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
        }
    }
*/
    public String getDateString() {
        long tsLong;
        tsLong = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd_HHmm_ss_SSS");//"MMM dd,yyyy HH:mm:ss");
        Date resultdate = new Date(tsLong);
        String rsltDate = sdf.format(resultdate);
        return rsltDate;
    }


    public void initMessageFiles(MessageFiles csvFile){
        //Initilizing CSV File with stored preferences
        String extStore = System.getenv("EXTERNAL_STORAGE");
        File f_exts = new File(extStore);

        String dirStr = f_exts.getAbsolutePath() + File.separator + "CowSync";  //getResources().getString(R.string.app_name);

        String prefixStr;
        String folderStr;
        int timeFileGenerator;

        prefixStr = sharedPref.getString("cow_file_prefix", "ADAS");
        folderStr = sharedPref.getString("cow_folder",getResources().getString(R.string.app_name));
        timeFileGenerator = Integer.valueOf(sharedPref.getString("cow_period_files", "5" ));

        sensorsCSV = new MessageFiles(prefixStr+ "S", folderStr);
        sensorsCSV.setParentDirStr(dirStr);
        //fileCSV.setFilePathTextView(filePathTxt);
        sensorsCSV.initTimerGenerator(timeFileGenerator);



    }

    public float getAX() {
        return AX;
    }

    public float getAY() {
        return AY;
    }

    public float getAZ() {
        return AZ;
    }

    public float getGX() {
        return GX;
    }

    public float getGY() {
        return GY;
    }

    public float getGZ() {
        return GZ;
    }

    public float getAnX() { return AnX; }

    public float getAnY() { return AnY; }

    public float getAnZ() { return AnZ; }

    public String getTs() { return ts;}

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Sensors Service Destroyed and STOPPED");
        sensorsCSV.stopFiles();
        if(sensHandler!=null)
            sensHandler.removeCallbacks(sensorsRunnable); //Remove possible conflicts
        super.onDestroy();
        //deviceConnected = false; //make sure it is false
    }

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

        LAT = location.getLatitude();
        LON = location.getLongitude();

       // textLat.setText(String.valueOf(LAT));
       // textLon.setText(String.valueOf(LON));

        LocAltitude=0; //gpsapp.getNoSats();

        //tvEdoGPS.setText("Conexión con GPS Disponible");
        //EDOGPSBoo = true;

        isGPSavailable = true;

        Speed= location.getSpeed();

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
