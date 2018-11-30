package com.adans.app_10.ADAS;

import com.adans.app_10.Cowtech54.Util;

import static java.lang.StrictMath.abs;

public class Dif {


    public static double[] Deltas(double[] velInterp, double[] timeRPM) {
        double[] AcelInter=new double[velInterp.length];
        double factorMetros = 1/3.6;
        double factorKmH2 = 12960;
        for (int i=0;i<=velInterp.length-2;i++){
            AcelInter[i]=((velInterp[i+1]-velInterp[i]) * factorMetros /(timeRPM[i+1]-timeRPM[i]));
            //AcelInter[i] = AcelInter[i]*factorKmH2;
        }
        return AcelInter;
    }
    //Array of FC
    public static double[] fuelConsArray(double[] velInterp, double[] timeRPM) {
        double[] FuelC=new double[velInterp.length];
        double FuelCAcum=0;
        double acel[] = Deltas(velInterp, timeRPM);
        int len = velInterp.length;

        for(int c=0;c<= velInterp.length-2;c++){
            //Inc Inicial
            double IncI=0.075;
            FuelC[c]=10.12+1.098*IncI+5.901*acel[c]-0.844*velInterp[c]+0.0354*Math.pow(velInterp[c],2)-0.0003*Math.pow(velInterp[c],3);

            if( Double.isNaN(FuelC[c]) ) {
                FuelC[c] = 0.0;
            }

        }

        return FuelC;
    }

    /**
     *
     * @param velInterp
     * @param timeRPM
     * @return averageFuelConsumption
     */
    public static double fuelConsumption(double[] velInterp, double[] timeRPM) {
        double[] FuelC=new double[velInterp.length];
        double FuelCAcum=0;
        double acel[] = Deltas(velInterp, timeRPM);
        int len = velInterp.length;

        //Inc Inicial
        double IncI=0.075;
        for(int c=0;c<= len -2;c++){

            FuelC[c]=10.12+1.098*IncI+5.901*acel[c]-0.844*velInterp[c]+0.0354*Math.pow(velInterp[c],2)-0.0003*Math.pow(velInterp[c],3);

            //If not NaN add it
            if( !Double.isNaN(FuelC[c]) )
                FuelCAcum = FuelCAcum+ FuelC[c];
        }

        double FuelProm = FuelCAcum/(len-2);

        return FuelProm;
    }

    public static double[] Kmetros(double[] velInterp, double[] timeRPM) {

        double[] DistArry=new double[velInterp.length];
        for(int i=0;i<=velInterp.length-5;i++) {
            DistArry[i]=((abs(velInterp[i+1]-velInterp[i]))*(timeRPM[i+1]-timeRPM[i]));
        }
        return DistArry;
    }

    public static double[] distTotalArray(double[] velInterp, double[] timeRPM){
        double distTot = 0;
        int len = velInterp.length;
        double distArray[] = new double[len];
        double dist = 0;
        double factorMetros = 1/3.6;
        for(int i=0; i<len-2; i++){
            // (V_i+1 - v_i)*(T_i+2 - T_i)
            if(velInterp[i+1]!= 0 && velInterp[i] != 0)
                dist = (abs(velInterp[i+1]+velInterp[i]))*(timeRPM[i+1]-timeRPM[i])/2 * factorMetros;

                if( !Double.isNaN(dist) ) {
                    distArray[i] = dist;
                }
        }

        return distArray;
    }

    public static double distTotal(double[] velInterp, double[] timeRPM){
        double distTot = 0;
        int len = velInterp.length;
        double factorMetros = 1/3.6;
        for(int i=0; i<len-2; i++){
                            // (V_i+1 - v_i)*(T_i+2 - T_i)
            if(velInterp[i+1]!= 0 && velInterp[i] != 0)
            distTot += (abs(velInterp[i+1]+velInterp[i]))*(timeRPM[i+1]-timeRPM[i])/2 * factorMetros;
        }

        return distTot; //meters
    }

    public static double[] distInst(double[] velInterp, double[] timeRPM){

        int len = velInterp.length;
        double[] distInst = new double[len];

        for(int i=0; i<len-2; i++){
            // (V_i+1 - v_i)*(T_i+2 - T_i)
            if(velInterp[i+1]!= 0 && velInterp[i] != 0)
                distInst[i] = (abs(velInterp[i+1]-velInterp[i]))*(timeRPM[i+1]-timeRPM[i]);
        }

        return distInst;
    }

    public static double emissions(double[] velInterp, double[] timeRPM){
        double distTot = distTotal(velInterp, timeRPM);
        double emission = 0;
        double acel[] = Deltas(velInterp, timeRPM);
        double FuelCons = fuelConsumption(velInterp,acel);

        emission = (1/((FuelCons*100)/(distTot)))*(8887/3.7854);

        return emission;
    }

    public static double emissions(double[] velInterp, double[] timeRPM,  double fuelCons){
        double distTot = distTotal(velInterp, timeRPM) / 1000.0; //m to km
        double emission = 0;
        double FuelCons = fuelCons;
                    //100 bc l/100km?
        //TODO: Emissions variable factor of CO2 depending of gas
        emission = (distTot*(FuelCons/100))*(8887/3.7854);

        return emission;
    }

    //Obtain the array of gears in time
    public static double[] gears( double[] velInterp, double[] RPM, double[] timeRPM){
        double ts = 0.5; //0.5 seconds
        double[] timeRPM_ts = tsArray(timeRPM, ts);
        double[] vel_ts = Util.interpLinear(timeRPM, velInterp, timeRPM_ts);
        double[] RPM_ts = Util.interpLinear(timeRPM, RPM, timeRPM_ts);
        int len = timeRPM_ts.length;
        double[] gear = new double[len];
        double[] Gears = new double[len];
        double gear_i;

        for(int i = 0; i< len  ; i++) {
            gear_i = vel_ts[i]/RPM_ts[i];

            if( !Double.isNaN(gear_i) &&  !Double.isInfinite(gear_i ) ) {
                gear[i] = gear_i;
            }
            else
                gear[i] = 0;

            if (gear[i]<=0.01) { Gears[i]=1;}
            if (gear[i]>0.01&&gear[i]<=0.02) { Gears[i]=2;}
            if (gear[i]>0.02&&gear[i]<=0.03) { Gears[i]=3;}
            if (gear[i]>0.03&&gear[i]<=0.04) { Gears[i]=4;}
            if (gear[i]>0.04&&gear[i]<=0.05) { Gears[i]=5;}
            if (gear[i]>0.05&&gear[i]<=0.08) { Gears[i]=6;}
        }


        return Gears;
    }

    public static int[] gearChanges(double[] Gears){
        double ts = 0.5; //0.5 seconds
        double timeWindow = 4.0; //seconds
        int len = Gears.length;

        double samples = timeWindow/ts;
        int samples_ = (int)samples;
        int lenFreq = len/samples_+1;
        int[] gearChanges_ = new int[lenFreq];

        int it = 0;
        int Cd=0;
        int Cdp=0;
        double gi, gip1;
        for(int i = 0; i<len-1 ; i++){
            gip1 = Gears[i+1];
            gi = Gears[i];
            if (gi != gip1){Cd++;}//If there is a change of gear in 2 contiguous
            //0 excep
            if(i % samples_ == samples_-1 && i>0 ){
                gearChanges_[Cdp]=Cd;
                Cdp++;
                Cd=0;
            }

        }
        //int[] gF = gearFreq;
        return gearChanges_;
    }

    public static int gearsMaxChanges(int[] changes){
        int len = changes.length;
        int max=0;
        for( int i = 0; i<len; i++){
            if( changes[i]>max) max = changes[i];
        }
        return max;
    }
    public static int gearsMaxChanges(double[] velInterp, double[] RPM, double[] timeRPM){

        double[] gears_ = gears(velInterp, RPM, timeRPM);
        int[] changes = gearChanges(gears_);
        int len = changes.length;
        int max=0;
        for( int i = 0; i<len; i++){
            if( changes[i]>max) max = changes[i];
        }
        return max;
    }


    //Generates from an array a new array with the timeStep specified in SECONDS
    public static double[] tsArray(double[] array, double ts){

        int len = array.length;
        double ti = array[0];
        double tf = array[len - 1];
        double period =  tf - ti;
        double samples = period/ts;
        int newLen = (int)samples;
        double[] tsArray_ = new double[newLen];

        if( newLen>2 ) {
            for(int i = 0; i<newLen  ; i++){
                tsArray_[i]= ti + ts*i;
            }
        }

        return tsArray_;

    }






}
