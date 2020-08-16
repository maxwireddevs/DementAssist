package com.wireddevs.dementassist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dpro.widgets.OnWeekdaysChangeListener;
import com.dpro.widgets.WeekdaysPicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wireddevs.dementassist.Alarm.Alarm;
import com.wireddevs.dementassist.Alarm.AlarmHelper;
import com.wireddevs.dementassist.Alarm.AlarmReceiver;
import com.wireddevs.dementassist.Utils.RecyclerViewItemClickListener;
import com.wireddevs.dementassist.Utils.TimeLongHelper;
import com.yanzhenjie.wheel.OnWheelChangedListener;
import com.yanzhenjie.wheel.WheelView;
import com.yanzhenjie.wheel.adapters.ArrayWheelAdapter;
import com.yanzhenjie.wheel.adapters.WheelViewAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class AlarmFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private String selectedattendancehour;
    private String selectedattendanceminute;
    private String currentdate,customtimestamp;
    private int multiplier=1000*60;
    private String addition="minutes";
    private ArrayList<Alarm> alarms;
    private char[] daysOfWeek=new char[7];
    private MyAlarmRecyclerViewAdapter adapter;
    private int logoid;
    private ArrayList<Integer>logolist=new ArrayList<>();
    private TextView addtext;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AlarmFragment() {
    }

    @SuppressWarnings("unused")
    public static AlarmFragment newInstance(int columnCount) {
        AlarmFragment fragment = new AlarmFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_list, container, false);

        addtext=new TextView(getActivity());

        RecyclerView v=view.findViewById(R.id.list);
        final RelativeLayout rl=view.findViewById(R.id.rl);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.floatingactionbutton);
        fab.show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmDialog();
            }
        });

        logolist.add(R.drawable.ic_notifications_black_24dp);
        logolist.add(R.drawable.ic_airline_seat_individual_suite_black_24dp);
        logolist.add(R.drawable.ic_cake_black_24dp);
        logolist.add(R.drawable.ic_child_friendly_black_24dp);
        logolist.add(R.drawable.ic_account_balance_black_24dp);
        logolist.add(R.drawable.ic_directions_black_24dp);
        logolist.add(R.drawable.ic_weekend_black_24dp);
        logolist.add(R.drawable.ic_kitchen_black_24dp);
        logolist.add(R.drawable.ic_healing_black_24dp);
        logolist.add(R.drawable.ic_local_florist_black_24dp);
        logolist.add(R.drawable.ic_restaurant_black_24dp);

        for(int i=0;i<7;i++){
            daysOfWeek[i]='1';
        }

        alarms=new ArrayList<>();
        final AlarmHelper ah=new AlarmHelper(getActivity());
        Cursor res= ah.getAllData();
        Log.v("AlarmFragment","Itemcount:" +ah.getItemCount());
        while(res.moveToNext()){
            alarms.add(new Alarm(res.getString(0),res.getLong(1),res.getInt(2),res.getLong(3),res.getString(4),res.getInt(5)));
        }

        addtext.setTextSize(20);
        addtext.setTextColor(Color.BLACK);
        addtext.setAlpha((float)0.50);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addtext.setText("No alarms found");
        addtext.setLayoutParams(layoutParams);
        rl.addView(addtext);
        addtext.setVisibility(View.INVISIBLE);

        if(alarms.size()==0){
            addtext.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams textlayoutparams = (RelativeLayout.LayoutParams)addtext.getLayoutParams();
            textlayoutparams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            addtext.setLayoutParams(textlayoutparams);
            Toast.makeText(getActivity(), "No alarms found, add alarm using the button.", Toast.LENGTH_SHORT).show();
        }

        // Set the adapter
        if (v != null) {
            Context context = v.getContext();
            if (mColumnCount <= 1) {
                v.setLayoutManager(new LinearLayoutManager(context));
            } else {
                v.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter=new MyAlarmRecyclerViewAdapter(alarms,mListener,getActivity());
            v.setAdapter(adapter);
            v.addOnItemTouchListener(new RecyclerViewItemClickListener(getActivity(), v, new RecyclerViewItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, final int position) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Edit Alarm: "+alarms.get(position).getName());
                    builder.setCancelable(true);

                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View editview= inflater.inflate(R.layout.alarm_dialog, null);
                    builder.setView(editview);

                    final RecyclerView rv=editview.findViewById(R.id.alarmlogorv);
                    final AlarmLogoListRecyclerViewAdapter adapterq=new AlarmLogoListRecyclerViewAdapter(getActivity(),logolist);
                    rv.setAdapter(adapterq);

                    for(int i=0;i<logolist.size();i++){
                        if(logolist.get(i)==alarms.get(position).getLogo()){
                            adapterq.setSelected(i);
                        }
                    }

                    String wds=alarms.get(position).getDaysinweek();
                    daysOfWeek=wds.toCharArray();

                    WeekdaysPicker widget = (WeekdaysPicker) editview.findViewById(R.id.weekdays);

                    LinkedHashMap<Integer, Boolean> map = new LinkedHashMap<>();
                    map.put(Calendar.SUNDAY, daysOfWeek[0] == '1');
                    map.put(Calendar.MONDAY, daysOfWeek[1] == '1');
                    map.put(Calendar.TUESDAY, daysOfWeek[2] == '1');
                    map.put(Calendar.WEDNESDAY, daysOfWeek[3] == '1');
                    map.put(Calendar.THURSDAY, daysOfWeek[4] == '1');
                    map.put(Calendar.FRIDAY, daysOfWeek[5] == '1');
                    map.put(Calendar.SATURDAY, daysOfWeek[6] == '1');

                    widget.setCustomDays(map);

                    widget.setOnWeekdaysChangeListener(new OnWeekdaysChangeListener() {
                        @Override
                        public void onChange(View view, int clickedDayOfWeek, List<Integer> selectedDays) {
                            if(daysOfWeek[clickedDayOfWeek-1]=='1'){
                                daysOfWeek[clickedDayOfWeek-1]='0';
                            }
                            else{
                                daysOfWeek[clickedDayOfWeek-1]='1';
                            }
                        }
                    });

                    final Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(alarms.get(position).getTimestamp());
                    SimpleDateFormat currenthour=new SimpleDateFormat("HH");
                    SimpleDateFormat currentminute=new SimpleDateFormat("mm");
                    selectedattendancehour=currenthour.format(c.getTime());
                    selectedattendanceminute=currentminute.format(c.getTime());
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);
                    WheelView hourswheelview=editview.findViewById(R.id.hours_wheelview);
                    WheelView minuteswheelview=editview.findViewById(R.id.minutes_wheelview);
                    final String[] hours = getResources().getStringArray(R.array.hours);
                    final String[] minutes=getResources().getStringArray(R.array.minutes);
                    WheelViewAdapter hourswheelviewadapter=new ArrayWheelAdapter<>(getActivity(), hours);
                    WheelViewAdapter minuteswheelviewadapter=new ArrayWheelAdapter<>(getActivity(), minutes);
                    hourswheelview.setAdapter(hourswheelviewadapter);
                    minuteswheelview.setAdapter(minuteswheelviewadapter);
                    hourswheelview.setCurrentItem(hour);
                    minuteswheelview.setCurrentItem(minute);
                    hourswheelview.setCyclic(true);
                    minuteswheelview.setCyclic(true);
                    hourswheelview.addChangingListener(new OnWheelChangedListener() {
                        @Override
                        public void onChanged(WheelView wheel, int oldValue, int newValue) {
                            selectedattendancehour=hours[newValue];
                            c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(selectedattendancehour));
                        }
                    });
                    minuteswheelview.addChangingListener(new OnWheelChangedListener() {
                        @Override
                        public void onChanged(WheelView wheel, int oldValue, int newValue) {
                            selectedattendanceminute=minutes[newValue];
                            c.set(Calendar.MINUTE, Integer.parseInt(selectedattendanceminute));
                        }
                    });
                    TimeLongHelper th=new TimeLongHelper(0,alarms.get(position).getInterval());

                    Spinner intervalspinner=(Spinner)editview.findViewById(R.id.intervalspinner);
                    intervalspinner.setSelection(th.getTypeInt());
                    intervalspinner.setOnItemSelectedListener(AlarmFragment.this);

                    final EditText alarmname=(EditText)editview.findViewById(R.id.editname);
                    final EditText alarmduration=(EditText)editview.findViewById(R.id.alarmintervaledit);

                    alarmname.setText(alarms.get(position).getName());
                    alarmduration.setText(String.valueOf(th.getValue()));

                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                            Intent updateServiceIntent = new Intent(getActivity(), AlarmReceiver.class);
                            PendingIntent pendingUpdateIntent = PendingIntent.getBroadcast(getActivity(), alarms.get(position).getCode(), updateServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarmManager.cancel(pendingUpdateIntent);
                            pendingUpdateIntent.cancel();
                            ah.deleteItem(alarms.get(position).getCode());
                            alarms.remove(position);
                            adapter.notifyDataSetChanged();
                            StringBuilder sb=new StringBuilder();
                            for(int i=0;i<7;i++){
                                sb.append(daysOfWeek[i]);
                            }
                            alarmSet(alarmname.getText().toString(),c.getTimeInMillis(),Integer.parseInt(alarmduration.getText().toString())*multiplier,sb.toString(),logolist.get(adapterq.getSelected()),alarmduration.getText().toString());
                            Date date = c.getTime();
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            String strDate = dateFormat.format(date);
                            addtext.setVisibility(View.INVISIBLE);
                        }
                    });

                    builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                            Intent updateServiceIntent = new Intent(getActivity(), AlarmReceiver.class);
                            PendingIntent pendingUpdateIntent = PendingIntent.getBroadcast(getActivity(), alarms.get(position).getCode(), updateServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarmManager.cancel(pendingUpdateIntent);
                            pendingUpdateIntent.cancel();
                            ah.deleteItem(alarms.get(position).getCode());
                            alarms.remove(position);
                            adapter.notifyDataSetChanged();
                            if(alarms.size()==0){
                                addtext.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
                @Override
                public void onLongItemClick(View view, int position) {}
            }));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                multiplier=1000*60;
                addition="minutes";
                break;
            case 1:
                multiplier=1000*60*60;
                addition="hours";
                break;
            case 2:
                multiplier=1000*60*60*24;
                addition="days";
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Alarm item);
    }

    private void alarmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Alarm");
        builder.setCancelable(true);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view= inflater.inflate(R.layout.alarm_dialog, null);
        builder.setView(view);

        final RecyclerView rv=view.findViewById(R.id.alarmlogorv);
        final AlarmLogoListRecyclerViewAdapter adapter=new AlarmLogoListRecyclerViewAdapter(getActivity(),logolist);
        rv.setAdapter(adapter);

        final WeekdaysPicker widget = (WeekdaysPicker) view.findViewById(R.id.weekdays);

        LinkedHashMap<Integer, Boolean> map = new LinkedHashMap<>();
        map.put(Calendar.SUNDAY, true);
        map.put(Calendar.MONDAY, true);
        map.put(Calendar.TUESDAY, true);
        map.put(Calendar.WEDNESDAY, true);
        map.put(Calendar.THURSDAY, true);
        map.put(Calendar.FRIDAY, true);
        map.put(Calendar.SATURDAY, true);

        widget.setCustomDays(map);
        widget.setOnWeekdaysChangeListener(new OnWeekdaysChangeListener(){
            @Override
            public void onChange(View view, int clickedDayOfWeek, List<Integer> selectedDays) {
                if(daysOfWeek[clickedDayOfWeek-1]=='1'){
                    daysOfWeek[clickedDayOfWeek-1]='0';
                }
                else{
                    daysOfWeek[clickedDayOfWeek-1]='1';
                }
            }
        });

        final Calendar c = Calendar.getInstance();
        SimpleDateFormat currenthour=new SimpleDateFormat("HH");
        SimpleDateFormat currentminute=new SimpleDateFormat("mm");
        selectedattendancehour=currenthour.format(c.getTime());
        selectedattendanceminute=currentminute.format(c.getTime());
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        WheelView hourswheelview=view.findViewById(R.id.hours_wheelview);
        WheelView minuteswheelview=view.findViewById(R.id.minutes_wheelview);
        final String[] hours = getResources().getStringArray(R.array.hours);
        final String[] minutes=getResources().getStringArray(R.array.minutes);
        WheelViewAdapter hourswheelviewadapter=new ArrayWheelAdapter<>(getActivity(), hours);
        WheelViewAdapter minuteswheelviewadapter=new ArrayWheelAdapter<>(getActivity(), minutes);
        hourswheelview.setAdapter(hourswheelviewadapter);
        minuteswheelview.setAdapter(minuteswheelviewadapter);
        hourswheelview.setCurrentItem(hour);
        minuteswheelview.setCurrentItem(minute);
        hourswheelview.setCyclic(true);
        minuteswheelview.setCyclic(true);
        hourswheelview.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                selectedattendancehour=hours[newValue];
                c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(selectedattendancehour));
            }
        });
        minuteswheelview.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                selectedattendanceminute=minutes[newValue];
                c.set(Calendar.MINUTE, Integer.parseInt(selectedattendanceminute));
            }
        });

        Spinner intervalspinner=(Spinner)view.findViewById(R.id.intervalspinner);
        intervalspinner.setOnItemSelectedListener(this);

        final EditText alarmname=(EditText)view.findViewById(R.id.editname);
        final EditText alarmduration=(EditText)view.findViewById(R.id.alarmintervaledit);


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(alarmname.getText().toString().length()>0){
                    try{
                        StringBuilder sb=new StringBuilder();
                        for(int i=0;i<7;i++){
                            sb.append(daysOfWeek[i]);
                        }
                        alarmSet(alarmname.getText().toString(),c.getTimeInMillis(),Integer.parseInt(alarmduration.getText().toString())*multiplier,sb.toString(),logolist.get(adapter.getSelected()),alarmduration.getText().toString());
                        Date date = c.getTime();
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String strDate = dateFormat.format(date);
                        addtext.setVisibility(View.INVISIBLE);

                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getActivity(), "Name field cannot be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
    }

    private void alarmSet(String name,long timeset,long interval,String weekly,int logo,String alarmduration){
        Random r=new Random();
        int nextt=r.nextInt(10000);
        if(timeset<Calendar.getInstance().getTimeInMillis()){
            timeset+=1000*60*60*24;
        }
        AlarmHelper ah=new AlarmHelper(getActivity());
        ah.insertItem(name,timeset,nextt,interval,weekly,logo);
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        intent.putExtra("requestCode", nextt);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),nextt, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= 23) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC, timeset, pendingIntent);
        }
        else {
            am.setExact(AlarmManager.RTC,timeset,pendingIntent);
        }
        alarms.add(new Alarm(name,timeset,nextt,interval,weekly,logo));
        adapter.notifyDataSetChanged();
        final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Toast.makeText(getActivity(), "Alarm is set: "+sdf.format(new Date(timeset))+"\ninterval: "+alarmduration+" "+addition, Toast.LENGTH_SHORT).show();
    }
}
