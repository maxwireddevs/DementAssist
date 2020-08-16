package com.wireddevs.dementassist;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dpro.widgets.WeekdaysPicker;
import com.wireddevs.dementassist.Alarm.Alarm;
import com.wireddevs.dementassist.AlarmFragment.OnListFragmentInteractionListener;
import com.wireddevs.dementassist.Utils.TimeLongHelper;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class MyAlarmRecyclerViewAdapter extends RecyclerView.Adapter<MyAlarmRecyclerViewAdapter.ViewHolder> {

    private final List<Alarm> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context context;

    public MyAlarmRecyclerViewAdapter(List<Alarm> items, OnListFragmentInteractionListener listener,Context context) {
        mValues = items;
        mListener = listener;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_alarm, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(mValues.get(position).getTimestamp());
        String timestamp=dateFormat.format(date);

        holder.mItem = mValues.get(position);
        holder.alarmname.setText(mValues.get(position).getName());
        holder.alarmtime.setText(String.format("Next alarm at: %s",timestamp.substring(11,16)));
        TimeLongHelper th=new TimeLongHelper(0,mValues.get(position).getInterval());
        holder.alarminterval.setText(String.format("Interval: %s",th.getValue()+" "+th.getTypeString()));
        holder.alarmlogo.setImageDrawable(context.getResources().getDrawable(mValues.get(position).getLogo()));

        String wds = mValues.get(position).getDaysinweek();
        char[] daysOfWeek=wds.toCharArray();

        LinkedHashMap<Integer, Boolean> map = new LinkedHashMap<>();
        map.put(Calendar.SUNDAY, daysOfWeek[0] == '1');
        map.put(Calendar.MONDAY, daysOfWeek[1] == '1');
        map.put(Calendar.TUESDAY, daysOfWeek[2] == '1');
        map.put(Calendar.WEDNESDAY, daysOfWeek[3] == '1');
        map.put(Calendar.THURSDAY, daysOfWeek[4] == '1');
        map.put(Calendar.FRIDAY, daysOfWeek[5] == '1');
        map.put(Calendar.SATURDAY, daysOfWeek[6] == '1');
        holder.weekdaydisplay.setCustomDays(map);
        holder.weekdaydisplay.setEditable(false);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView alarmname;
        public final TextView alarmtime;
        public final TextView alarminterval;
        public final ImageView alarmlogo;
        public final WeekdaysPicker weekdaydisplay;
        public Alarm mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            alarmname = (TextView) view.findViewById(R.id.alarmname);
            alarmtime = (TextView) view.findViewById(R.id.alarmtimeremaining);
            alarminterval = (TextView) view.findViewById(R.id.alarminterval);
            alarmlogo = (ImageView) view.findViewById(R.id.alarmlogo);
            weekdaydisplay=(WeekdaysPicker) view.findViewById(R.id.weekdaysd);
        }
    }
}
