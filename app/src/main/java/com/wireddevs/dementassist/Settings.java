package com.wireddevs.dementassist;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.wireddevs.dementassist.Contacts.Contacts;
import com.wireddevs.dementassist.Contacts.ContactsHelper;

import java.util.ArrayList;


public class Settings extends Fragment{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private int currentimg=0;
    private ArrayList<Integer> imgList=new ArrayList<>();
    public Settings() {
        // Required empty public constructor
    }

    public static Settings newInstance(String param1, String param2) {
        Settings fragment = new Settings();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.floatingactionbutton);
        fab.hide();

        Button tutorialbutton=(Button)view.findViewById(R.id.tutorialbutton);
        tutorialbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                imgList.add(R.drawable.tutorial1);
                imgList.add(R.drawable.tutorial2);
                imgList.add(R.drawable.tutorial3);
                builder.setCancelable(true);
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view= inflater.inflate(R.layout.tutorial_dialog, null);
                builder.setView(view);

                final ImageView tutorialimg=view.findViewById(R.id.tutorialimg);
                tutorialimg.setImageDrawable(getResources().getDrawable(imgList.get(currentimg)));
                Button prev=view.findViewById(R.id.prevbutton);
                final Button next=view.findViewById(R.id.nextbutton);

                final Dialog dialog=builder.show();

                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentimg++;
                        if(currentimg<=imgList.size()-1){
                            tutorialimg.setImageDrawable(getResources().getDrawable(imgList.get(currentimg)));
                        }
                        else if(currentimg==imgList.size()){
                            next.setText("End");
                            dialog.cancel();
                        }
                    }
                });

                prev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(currentimg>0){
                            currentimg--;
                            tutorialimg.setImageDrawable(getResources().getDrawable(imgList.get(currentimg)));
                        }
                    }
                });
            }
        });

        SharedPreferences alarmtype= getActivity().getSharedPreferences("alarmtype",Context.MODE_PRIVATE);
        int type=alarmtype.getInt("alarmtype",0);
        Spinner alarmtypespinner=view.findViewById(R.id.alarmtypespinner);
        alarmtypespinner.setSelection(type);
        alarmtypespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences alarmtype= getActivity().getSharedPreferences("alarmtype",Context.MODE_PRIVATE);
                SharedPreferences.Editor edit=alarmtype.edit();
                edit.putInt("alarmtype",position);
                edit.apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        SharedPreferences alarmsound=getActivity().getSharedPreferences("alarmsound",Context.MODE_PRIVATE);
        int soundtype=alarmsound.getInt("alarmsound",0);
        Spinner alarmsoundspinner=view.findViewById(R.id.alarmsoundspinner);

        if(soundtype==R.raw.alarm1){
            alarmsoundspinner.setSelection(0);
        }
        else if(soundtype==R.raw.alarm2){
            alarmsoundspinner.setSelection(1);
        }
        else if(soundtype==R.raw.alarm3){
            alarmsoundspinner.setSelection(2);
        }

        alarmsoundspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences alarmsound= getActivity().getSharedPreferences("alarmsound",Context.MODE_PRIVATE);
                SharedPreferences.Editor edit=alarmsound.edit();
                switch (position){
                    case 0:
                        edit.putInt("alarmsound",R.raw.alarm1);
                        break;
                    case 1:
                        edit.putInt("alarmsound",R.raw.alarm2);
                        break;
                    case 2:
                        edit.putInt("alarmsound",R.raw.alarm3);
                        break;
                }
                edit.apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;
    }

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
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void sharedPreferenceEdit(SharedPreferences sp,String spt,String edittext){
        SharedPreferences.Editor videoswatchededitor=sp.edit();
        videoswatchededitor.putString(spt,edittext);
        videoswatchededitor.apply();
    }
}
