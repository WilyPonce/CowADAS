package com.adans.app_10.Cowtech54;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.adans.app_10.ADAS.Dif;

import static com.adans.app_10.Cowtech54.Util.linked2Array;

/**
 * Created by Wily on 09/03/2018.
 */


public class BtMessageManager {

    private final String TAG = BtMessageManager.class.getSimpleName();

    public static String Message;
    public static Set<String> IDsListSet;
    public static ArrayList<String> IndivIDsArrayListStr;
    public static ArrayList<IDObject> MatIDs;
    public int lostIDs=0;
    public int recIDs=0;
    public static String MessagePurged = "";
    public static String MessagePurgedCopy = "";

    //For algorithms
    public LinkedList<Number> timeVel = new LinkedList<Number>();
    public LinkedList<Number> vel = new LinkedList<Number>();
    public LinkedList<Number> timeRPM = new LinkedList<Number>();
    public LinkedList<Number> RPM = new LinkedList<Number>();

    CowTabFragment1 CowFac1=new CowTabFragment1();
    private double fuelConsum = 10.5;
    private double emissions = 111;
    private double timePeriodVel = 0.1;
    private int drivingStyle = 0;

    // -- Constructors
    public BtMessageManager(){ //Default constructor
        Message = "";
        IDsListSet = new HashSet<String>();
        IndivIDsArrayListStr = new ArrayList<String>();
        if(MatIDs == null){
            initMatIDs();
        }
    }
    public BtMessageManager(String strMessage){ //c1
        Message = strMessage;
        IDsListSet = new HashSet<String>();
        IndivIDsArrayListStr = new ArrayList<String>();
        if(MatIDs == null){
            initMatIDs();
        }

        updateAll();
    }

    // -- Methods
    public void initMatIDs(){
        int maxID = 2300;
        MatIDs = new ArrayList<IDObject>();
        for(int i = 0 ; i<maxID ; i++){
            MatIDs.add(new IDObject(i));
        }
    }

    public void addNewMessage(String msgStr) {
        Message=msgStr;
        updateAll();
    }

    //Getters


    public double getTimePeriodVel() {
        return timePeriodVel;
    }

    public double getFuelConsum() {
        return fuelConsum;
    }

    public double getEmissions() {
        return emissions;
    }

    public int getDrivingStyle() {
        return drivingStyle;
    }



    public Set<String> getIDsListSet() {
        return IDsListSet;
    }

    public String getMessagePurged() {
        return MessagePurged;
    }

    public String getMessagePurgedCopy() {
        return MessagePurgedCopy;
    }

    public int getLostIDs() {
        return lostIDs;
    }

    public int getRecIDs() {
        return recIDs;
    }

    public void setIDsListSet(Set<String> IDsListSet) {
        this.IDsListSet = IDsListSet;
    }

    public ArrayList<String> getIndivIDsArrayListStr() {
        return IndivIDsArrayListStr;
    }

    public void setIndivIDsArrayListStr(ArrayList<String> indivIDsArrayListStr) {
        IndivIDsArrayListStr = indivIDsArrayListStr;
    }


    public void updateAll(){
        String[] messageSplitStr;
        //Data line from CowFracment 1
        messageSplitStr = Message.split("\n");
        MessagePurged = "";

        int mLength = messageSplitStr.length;
        for(int i = 0; i < mLength ; i++){
            //Check that contains [ & ]
            if( isACompleteID(messageSplitStr[i]) ){
                //Creating clean message with only good IDs and with out [ ]
                MessagePurged += "\n" + messageSplitStr[i];
                MessagePurged = MessagePurged.replaceAll("\\[" ,"");
                MessagePurged = MessagePurged.replaceAll("]","");


                //Splitting the message
                Number[] IDtram = Splitter2Num(messageSplitStr[i]);

                // [1] is the CAN ID positi
                Number currentIDNum = IDtram[1];
                String currentIDStr = IDtram[1].toString();

                //Add the ID to the Set List
                IDsListSet.add( currentIDStr );
                //Add Tram to the ID in mat
                MatIDs.get(currentIDNum.intValue()).addTram(IDtram);

                //Delete
                Number timePeriod = MatIDs.get(currentIDNum.intValue()).getTimePeriod();

                //Modifi
                //Log.d("MessageMannager: ", "Time period " + timePeriod);
                Number tP = 60.0;
                Number offset = 30.0; //Seconds to delete   
                if((double)timePeriod>(double)tP){
                    while((double)timePeriod>(double)tP - (double)offset) {//Delete first Offset sec
                        //Log.d("MessageMannager: ", "Removing items");
                        MatIDs.get(currentIDNum.intValue()).removeFirstItemInLists();
                        timePeriod = MatIDs.get(currentIDNum.intValue()).getTimePeriod();
                    }
                }

                //---- ADAS algorithms ------
                int numSamples = 30;
                int timeSample = 5000; //mSeconds

                timePeriodVel = 0;
                if(currentIDNum.intValue() == 2037){ //2037 = vel ID in COW for vehicles (no trucks j1939)
                    timeVel.add(IDtram[0]);
                    vel.add(IDtram[5]);
                    if(timeVel.size()>10)
                        timePeriodVel = (double)timeVel.getLast() - (double)timeVel.getFirst();
                    else
                        timePeriodVel = 0;
                }
                if(currentIDNum.intValue() == 2036) { //2036 = RPM ID in COW for vehicles (no trucks j1939)
                    timeRPM.add(IDtram[0]);
                    RPM.add(IDtram[5]);
                }

                //Execute algorithms if there is a Period of at least 5s, then refresh for next execution
                if(timePeriodVel >= timeSample){

                    //Init arrays for algorithms
                    double[] timeVel_ = new double[timeVel.size()];
                    double[] timeRPM_ = new double[timeRPM.size()];
                    double[] vel_ = new double[vel.size()];
                    double[] RPM_ = new double[RPM.size()];
                    double[] vel_interp;

                    double[] velAtRPM = new double[RPM.size()];
                    double[] acel = velAtRPM;


                    timeVel_= linked2Array(timeVel, timeVel_ );
                    timeRPM_=linked2Array(timeRPM, timeRPM_ );

                    timeRPM_ = Util.arrayDividedBy1000(timeRPM_);//time in seconds
                    timeVel_ = Util.arrayDividedBy1000(timeVel_);//time in seconds
                    // vel = Util.arrayXfactor(vel,1/3.6); //Km/h to m/s

                    vel_=linked2Array(vel, vel_ );
                    //RPM_=linked2Array(RPM, RPM_ );

                    vel_interp = Util.interpLinear(timeVel_,vel_ , timeRPM_);

//                    fuelConsum = Dif.fuelConsumption(vel_interp, timeRPM_);
//                    emissions = Dif.emissions(vel_interp, timeRPM_, fuelConsum);

                    fuelConsum = Dif.fuelConsumption(vel_, timeVel_);
                    emissions = Dif.emissions(vel_, timeVel_, fuelConsum);
                    if(RPM.size()>3) {
                        drivingStyle = Dif.gearsMaxChanges(vel_interp, RPM_, timeRPM_);

                        //Reinit var
                        timeVel = new LinkedList<Number>();
                        timeRPM = new LinkedList<Number>();
                        vel = new LinkedList<Number>();
                        RPM = new LinkedList<Number>();
                        //Store the last value at first
                        timeVel.add( timeVel_[timeVel_.length-1]*1000.0);
                        timeRPM.add( timeRPM_[timeRPM_.length-1]*1000.0);
                        vel.add(vel_[vel_.length-1]);
                        RPM.add(RPM_[RPM_.length-1]);
                    }
                    else
                        drivingStyle = 1;



                    /*
                    Log.d(TAG, "timeVel_: " + Util.doubleArray2String(timeVel_));
                    Log.d(TAG, "timeRPM_: " + Util.doubleArray2String(timeRPM_) );
                    Log.d(TAG, "vel_: " + Util.doubleArray2String(vel_));
                    Log.d(TAG, "vel_interp: " + Util.doubleArray2String(vel_interp));
                    */
                    
                }


                recIDs++;
            }else{
                if(messageSplitStr[i].length()>1)lostIDs++;
            }
        }
        //Delete first enter from message
        MessagePurged = MessagePurged.replaceFirst("\n","");
        //Coping message purged for future handle to avoid collision in above loop
        MessagePurgedCopy = MessagePurged;

    }

    //========



    public static ArrayList<String> getIndivIDs2Str(String StrMsg){
        String[] ArrOfIDs= StrMsg.split("\n");
        ArrayList<String> ArrayListOfIDs = new ArrayList<String>();

        int counter=0;
        for(int i = 0; i < ArrOfIDs.length; i++){

                if(isACompleteID(ArrOfIDs[i])){
                    ArrayListOfIDs.add( ArrOfIDs[i]);
                }
        }

        return ArrayListOfIDs;
    }



    public static Set<Number> getIDsListSet(String StrMsg){
        Set<Number> SetIDs = new HashSet<Number>();

        String[] ArrOfIDs= StrMsg.split("\n");
        for(int i = 0; i < ArrOfIDs.length; i++){
            // [1] is the CAN ID position
            Number IDtram = Splitter2Num(ArrOfIDs[i])[1];
            //System.out.println( IDtram );
            SetIDs.add(IDtram);
        }
        return SetIDs;
    }

    public static Set<Number> addTwoSets(Set<Number> one, Set<Number> two) {
        Set<Number> newSet = new HashSet<Number>(one);
        newSet.addAll(two);
        return newSet;
    }



    private static Boolean isACompleteID(String StrMsg){


        if( StrMsg.contains("[") && StrMsg.contains("]")
                && StrMsg.length()<100) {

            return true;
        }
        else //System.out.println("Message Incomplete: No beginning or ending brackets [ ]");
            return false;


    }

    /**
     * Split the message ID into individual numbers
     * @param str Array of Strings (IDs) given in a message
     */
    public static Number[] Splitter2Num(String str){
        Number[] NumArr={0,0,0,0,0,0,0,0,0,0,0};

        if(isACompleteID(str)){
            str=str.replace("[", "");
            str=str.replace("]", "");
            NumArr = StrSplitNum(str);
            return NumArr;
        }
        else return null;

    }

    /**
     * Converts a String of numbers into an Number Array
     * @param StrVar String with only numbers separated by TABs
     * @return Number[]
     */
    public static Number[] StrSplitNum(String StrVar){


        String[] arr = StrVar.split("\t");
        Number[] numArr = new Number[arr.length];
        try {
            for (int i = 0; i < numArr.length; i++) {
                //numArr[i] = Integer.parseInt(arr[i]);
                try {
                    if (arr[i].length() > 0 && arr[i].length() < 20) {
                        //numArr[i] = Integer.parseInt(arr[i]);

                        numArr[i] = Double.parseDouble(arr[i]);
                    } else numArr[i] = 0;
                }catch(NumberFormatException e){
                    e.printStackTrace();
                    return new Number[3];
                }

            }
        }catch(NumberFormatException e){
            e.printStackTrace();
            return new Number[3];
        }
        return numArr;
    }

    /*==============================================================*/



}
