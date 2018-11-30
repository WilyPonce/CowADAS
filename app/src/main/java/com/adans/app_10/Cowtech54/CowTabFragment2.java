package com.adans.app_10.Cowtech54;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adans.app_10.R;

import java.text.DecimalFormat;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CowTabFragment2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CowTabFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CowTabFragment2 extends Fragment implements ServiceStatusListener {
    public static ServiceStatusListener serviceStatusListener;


    private final String TAG = CowTabFragment2.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Service started
    boolean isServiceRunning;

    //Decimal Fotmat.
    DecimalFormat df = new DecimalFormat("#.00");

    //Var Emisissions
    double Emissions;
    double FuleAcum;
    double deltaAcum;
    double deltaprom;

    //Inst of Cow Service
    CowService cowService2;
    Disposable disposable;

    CowTabFragment1 cowfrac1;

    double[] DistTotal;
    int[] Gears;

    TextView tvPerfil,tvConsumo,tvEmis;
    //Button btnStartUpd;
    int C=0;


    int CTimer=0;


    double[]FpromAry;



    double[] velInterp, rpmInterp;
    double[] EmisAry;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private OnFragmentInteractionListener mListener;

    public CowTabFragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CowTabFragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static CowTabFragment2 newInstance(String param1, String param2) {
        CowTabFragment2 fragment = new CowTabFragment2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    protected ServiceConnection cowServerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            CowService.CowBinder binder2 = (CowService.CowBinder) service;
            cowService2 = binder2.getService();

            //Disposable for the subscriber Called from CowService to update the UI
            disposable = cowService2.observeString()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(string -> tvConsumo.setText(string[5]));
            disposable = cowService2.observeString()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(string -> tvEmis.setText(string[6]));
            disposable = cowService2.observeString()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(string -> tvPerfil.setText(string[7]));

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onBindingDied(ComponentName name) {

        }

    };

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        serviceStatusListener = this;

        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_cow_tab2, container, false);

        tvPerfil=(TextView)view.findViewById(R.id.tvPerfilFrac);
        tvConsumo=(TextView)view.findViewById(R.id.tvConsumoFrac);
        tvEmis=(TextView)view.findViewById(R.id.tvEmisFrac);
        //btnStartUpd=(Button)view.findViewById(R.id.btnSUpd);

        //Interp();
        //UpdLabels();

        return view;
    }



    private void UpdLabels() {

        if(Gears[CTimer]<=1){
            tvPerfil.setText("Calm");
            tvPerfil.setBackgroundColor(Color.BLUE);
        }
        if(Gears[CTimer]>1&&Gears[CTimer]<=3){
            tvPerfil.setText("Normal");
            tvPerfil.setBackgroundColor(Color.GREEN);
        }
        if(Gears[CTimer]>3&&Gears[CTimer]<=5){
            tvPerfil.setText("Sporty");
            tvPerfil.setBackgroundColor(Color.RED);
        }


        tvConsumo.setText(String.valueOf(df.format(FpromAry[CTimer])+" km/lt"));

        if(FpromAry[CTimer]<=4){
            tvConsumo.setBackgroundColor(Color.BLUE);
        }
        if(FpromAry[CTimer]>4&&FpromAry[CTimer]<=12){
            tvConsumo.setBackgroundColor(Color.GREEN);
        }
        if(FpromAry[CTimer]>12&&FpromAry[CTimer]<=20){
            tvConsumo.setBackgroundColor(Color.RED);
        }


        tvEmis.setText(String.valueOf(df.format(EmisAry[CTimer])+" gCO2"));

        if(EmisAry[CTimer]<=2){
            tvEmis.setBackgroundColor(Color.BLUE);
        }
        if(EmisAry[CTimer]>2&&EmisAry[CTimer]<=4){
            tvEmis.setBackgroundColor(Color.GREEN);
        }
        if(EmisAry[CTimer]>4&&EmisAry[CTimer]<=5){
            tvEmis.setBackgroundColor(Color.RED);
        }

    }




    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    @Override
    public void onServiceStatusChange(boolean status) {
        if(status) {
            isServiceRunning = true;
            Log.d(TAG, "Service is running" );
            Log.d(TAG, "Binding service" );
            Intent cowintent = new Intent(getApplicationContext(),CowService.class);
            getApplicationContext().bindService(cowintent, cowServerConn, Context.BIND_AUTO_CREATE);
        }
        else{
            if(isServiceRunning) {
                getApplicationContext().unbindService(cowServerConn);
                isServiceRunning = false;
            }
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void Interp() {


        //DS Pros
        double[] gear=new double[velInterp.length];
        for (int c=0;c<=velInterp.length-11;c++){
            gear[c]=velInterp[c]/rpmInterp[c];
        }
        Gears=new int[75];
        for (int c=0;c<=70;c++)
        {
            if (gear[c]<=0.01) { Gears[c]=1;}
            if (gear[c]>0.01&&gear[c]<=0.02) { Gears[c]=2;}
            if (gear[c]>0.02&&gear[c]<=0.03) { Gears[c]=3;}
            if (gear[c]>0.03&&gear[c]<=0.04) { Gears[c]=4;}
            if (gear[c]>0.04&&gear[c]<=0.05) { Gears[c]=5;}
        }
        int Cd=0;
        int Cdp=0;
        int[] CdC=new int[14];
        for(int c=0;c<70;c++){
            if (Gears[c+1]!=Gears[c]){Cd++;}//If there is a change of gear in 2 contiguous
            //0 excep
            if(c%5==0&&Gears[c]!=0){
                CdC[Cdp]=Cd;
                Cdp++;
                Cd=0;
            }
        }

    }

}
