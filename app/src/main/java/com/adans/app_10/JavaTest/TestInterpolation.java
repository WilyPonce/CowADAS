package com.adans.app_10.JavaTest;
import com.adans.app_10.Cowtech54.Util;
import com.adans.app_10.ADAS.Dif;

/**
 * Created by Wily on 17/05/2018.
 */

public class TestInterpolation {

    public static void main (String[] args){
//        double[] vel = {0,0,0,11,11,12,11,17,24,39,39,40,41,41,41,38,9,33,34,43,45,48,48,46,45,39,39,14,9,11,19,21,38,39,39,42,44,44,45,46,45,45,42,38,35,31,6,21,31,39,40,43,42,28,7,12,15,16,14,11,3};
//        double[] RPM = {645,790,1525.5,1781.5,1267.25,1232.25,995.75,907.25,800.25,744.5,1688,1910.25,1914.5,1951.75,2100.25,1777.25,1718,1105,1040.25,1003.75,929,912.75,806.25,773.75,1455.75,1950.75,2061.75,1951.25,1879.5,2020.75,2274,2257.25,2330.25,2366.75,2380.25,2394.5,2433.25,2507.25,2508,2458.5,2338.75,2360,1961.25,1936.5,1728.25,1558.5,1344,2039.75,2032.25,2054.25,2097.5,2199,2379.5,2485,2564.5,2496,2441.25,1474.75,998.5,955.5,1476.75,1491.25,1106,1205.5,635.75,932.5,932,810.75,781.5,782,693};
//        double[] timeVel = {3010956,3013023,3014210,3018012,3018425,3018838,3019322,3022696,3024089,3029773,3030173,3032026,3032598,3032918,3034029,3036538,3042324,3048734,3049136,3053224,3055332,3055986,3058435,3060531,3061673,3065100,3065418,3071064,3074557,3076665,3079171,3080051,3090543,3091255,3091662,3094102,3094896,3097757,3100247,3102400,3102700,3103570,3105769,3106657,3107143,3107628,3110809,3115272,3119063,3123460,3124252,3133275,3135385,3148708,3156154,3158326,3159221,3162106,3164397,3167321,3170301};
//        double[] timeRPM = {3015000,3015783,3023118,3025904,3030973,3032358,3038395,3039270,3040090,3041369,3045505,3047270,3047754,3048256,3049544,3050444,3053882,3056856,3059638,3061265,3063680,3064622,3070575,3072698,3076265,3078027,3078198,3081225,3081924,3082319,3084156,3085067,3086713,3088316,3089025,3089984,3090779,3092884,3093371,3095216,3095605,3096005,3096883,3098471,3102242,3104877,3106174,3113889,3116982,3117473,3117874,3119392,3122970,3125401,3130183,3134472,3134972,3136691,3138946,3139346,3147238,3147736,3150744,3152280,3154306,3156958,3157452,3165404,3167710,3168967,3171587};
        double[] vel = {     0,  0,  0,  0,  0, 17, 22, 28, 34, 39, 40, 41, 39, 38, 38, 38, 45, 45, 41, 43, 44, 48, 58, 58, 52, 48, 44, 38, 35, 35, 32, 34, 21, 16, 24, 24, 24, 22, 20, 18, 18, 10,  5,  6,  4,  3,  2,  2,  2,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0};
        double[] RPM = {1890.5,  1890.5, 1882.75, 1859.25, 1863.25, 1878.75,    2121, 2148.25, 2167.75, 1921.75, 2148.25,    2324, 1718.75,  1722.5, 1628.75,    1867,    2082,  1972.5,     992,   937.5,  878.75, 1468.75,  1562.5, 1320.25,    2125,  2187.5, 1507.75,  984.25,  2519.5,    2203,    2039,     949,     828,     785, 1878.75, 1605.25,  1679.5,  1519.5,     785,     742,  1640.5, 1863.25,  1011.5,  718.75,  718.75,  781.25,   851.5,     871, 1777.25,  1015.5,  718.75,  710.75,     707,  863.25,  863.25,  859.25,  859.25,     699,     699,     703,     707,  863.25,  878.75,     875,  859.25,  863.25,  773.25,     703,     703,   769.5};
        double[] timeVel = {     1308405, 1311463, 1313521, 1314944, 1315262, 1319713, 1324281, 1328232, 1331145, 1332931, 1333399, 1341257, 1342153, 1342404, 1343534, 1344183, 1349362, 1349897, 1353103, 1353934, 1355070, 1356509, 1361885, 1362265, 1365723, 1366445, 1367182, 1368943, 1369353, 1369789, 1371574, 1373073, 1376211, 1379597, 1382532, 1382931, 1389722, 1390045, 1391686, 1392759, 1393395, 1398059, 1401618, 1402285, 1403356, 1404102, 1405242, 1405810, 1406458, 1406953, 1407571, 1408303, 1409026, 1409353, 1410567, 1413170, 1413744, 1414716, 1415378, 1419544, 1421158, 1423920, 1427765, 1428083, 1428438, 1432271, 1433846, 1434500, 1435017, 1435191, 1438746, 1439063, 1439886, 1442898, 1445572, 1446138, 1448070, 1450679, 1451269};
        double[] timeRPM = {1309053 , 1309224 , 1309404 , 1310194 , 1310434 , 1310752 , 1312018 , 1312336 , 1313101 , 1317063 , 1317739 , 1317907 , 1321504 , 1321848 , 1322509 , 1325941 , 1327908 , 1332182 , 1334466 , 1335223 , 1337319 , 1338066 , 1339812 , 1340814 , 1345980 , 1346470 , 1350215 , 1351028 , 1352251 , 1353512 , 1361391 , 1363605 , 1365220 , 1376617 , 1378281 , 1379999 , 1380498 , 1385248 , 1392248 , 1394443 , 1400469 , 1400964 , 1402601 , 1407986 , 1408540 , 1411393 , 1411741 , 1412258 , 1416143 , 1418820 , 1421836 , 1422413 , 1423022 , 1430199 , 1430763 , 1431174 , 1431570 , 1436993 , 1437322 , 1438486 , 1439554 , 1440804 , 1442281 , 1442550 , 1444572 , 1444837 , 1447747 , 1448983 , 1450034 , 1454000};


        double[] Acel;
        double[] instDist;

        double[] velInterp, rpmInterp;
        timeRPM = Util.arrayDividedBy1000(timeRPM);
        timeVel = Util.arrayDividedBy1000(timeVel);
       // vel = Util.arrayXfactor(vel,1/3.6); //Km/h to m/s
        velInterp = Util.interpLinear(timeVel,vel,timeRPM);
        rpmInterp = Util.interpLinear(timeRPM,RPM,timeVel);
       // timeRPM = Util.arrayXfactor(timeRPM, 1/3600);

//        //DS Pros
//        double[] gear=new double[velInterp.length];
//        for (int c=0;c<=velInterp.length-11;c++){
//            gear[c]=velInterp[c]/rpmInterp[c];
//        }
//        int[] Gears=new int[75];
//        for (int c=0;c<=70;c++)
//        {
//            if (gear[c]<=0.01) { Gears[c]=1;}
//            if (gear[c]>0.01&&gear[c]<=0.02) { Gears[c]=2;}
//            if (gear[c]>0.02&&gear[c]<=0.03) { Gears[c]=3;}
//            if (gear[c]>0.03&&gear[c]<=0.04) { Gears[c]=4;}
//            if (gear[c]>0.04&&gear[c]<=0.05) { Gears[c]=5;}
//        }
//        int Cd=0;
//        int Cdp=0;
//        int[] CdC=new int[14];
//        for(int c=0;c<70;c++){
//
//            if (Gears[c+1]!=Gears[c]){Cd++;}
//
//            //0 excep
//            if(c%5==0&&Gears[c]!=0){
//                CdC[Cdp]=Cd;
//                Cdp++;
//                Cd=0;
//            }
//        }
//
//
//        Acel= Dif.Deltas(velInterp,timeRPM);
//        //AclInterp = Util.interpLinear(timeVel,Acel,timeRPM);
//
//        double[] FuleC;
//        FuleC=Dif.fuelConsArray(velInterp,Acel);
//        double FuleAcum=0;
//        int CdFA=0;
//        double[] FpromAry=new double[14];
//        for(int ct=0;ct<=velInterp.length-1;ct++) {
//            FuleAcum = FuleAcum + FuleC[ct];
//            if (ct % 5 == 0 && FuleC[ct] != 0) {
//                FpromAry[CdFA] = (FuleAcum + FuleC[ct])/5;
//                CdFA++;
//                FuleAcum = 0;
//            }
//        }
//        //FuleProm=100/(FuleAcum/(velInterp.length));
//
//
//        double FuleAcumMuestra=0;
//        for(int ct=0;ct<=39;ct++){
//            FuleAcumMuestra = FuleAcum+FuleC[ct];
//        }
//        double FulePromMuestra=100/(FuleAcumMuestra/(40));
//
//
//        int CdDP=0;
//        double DistAcum = 0;
//        double[] Distprom = new double[14];
//        double[] DistTotal=Dif.Kmetros(velInterp,timeRPM);
//        for (int c=0;c<=DistTotal.length-1;c++){
//            DistAcum=DistAcum+(DistTotal[c]/1000000);
//            if(c%5==0&&FuleC[c]!=0){
//                Distprom[CdDP]=(DistAcum+FuleC[c]/10000)/5;
//                CdDP++;
//                DistAcum=0;
//            }
//        }
        double fuelC = Dif.fuelConsumption(velInterp, timeRPM);
        double emissions = Dif.emissions(velInterp, timeRPM, fuelC);

        double fuelComArray[] =  Dif.fuelConsArray(velInterp, timeRPM);
        double sumFuelC = Util.sumInArray(fuelComArray);
        double dist = Dif.distTotal(velInterp,timeRPM);
        double distArray[] = Dif.distTotalArray(velInterp,timeRPM);
//        System.out.println("Size vel: " + vel.length);
//        System.out.println("Size RPM: " + RPM.length);
//        System.out.println("Size VelIntRPM: " + velInterp.length);
//        System.out.println("Size RPMIntVel" + rpmInterp.length);
//        System.out.println("Size TimeRPM" + timeRPM.length);

        //Gears
        double[] gearsData = Dif.gears(velInterp, RPM, timeRPM);
        int[] gearsChanges = Dif.gearChanges(gearsData);

        System.out.println("Fuee Cons (l/100km): " + fuelC);
        System.out.println("Emissions (gr CO2): " + emissions);
        System.out.println("Distance (m): " + dist);

        System.out.println("");
        System.out.println("Distance Array: ");
        for(int i = 0; i<distArray.length; i++){
            System.out.print(distArray[i]+", ");
        }

        System.out.println("");
        System.out.println("Vel Interp: ");
        for(int i = 0; i<velInterp.length; i++){
            System.out.print(velInterp[i]+", ");
        }
        System.out.println("");
        System.out.println("Rpm time: ");
        for(int i = 0; i<timeRPM.length; i++){
            System.out.print(timeRPM[i]+", ");
        }
        System.out.println("");
        System.out.println("Fuel Cons inst: ");
        for(int i = 0; i<fuelComArray.length; i++){
            System.out.print(fuelComArray[i]+", ");
        }


        System.out.println("");
        System.out.println("Fuel C ARRAY sum: "+ sumFuelC);

        System.out.println("");
        System.out.println("Gears[]: ");
        for(int i = 0; i<gearsData.length; i++){
            System.out.print(gearsData[i]+", ");
        }

        System.out.println("");
        System.out.println("GearsChanges by Time Windows[]: ");
        for(int i = 0; i<gearsChanges.length; i++){
            System.out.print(gearsChanges[i]+", ");
        }

        System.out.println("");
        System.out.println("GearsChanges max changes: ");

            System.out.print(Dif.gearsMaxChanges(gearsChanges)+" \n");
            System.out.print(Dif.gearsMaxChanges(velInterp, RPM, timeRPM)+" ");


    }
}
