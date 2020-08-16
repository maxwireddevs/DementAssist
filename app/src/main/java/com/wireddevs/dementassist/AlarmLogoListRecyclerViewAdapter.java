package com.wireddevs.dementassist;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wireddevs.dementassist.Alarm.Alarm;
import com.wireddevs.dementassist.AlarmFragment.OnListFragmentInteractionListener;
import com.wireddevs.dementassist.Utils.TimeLongHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class AlarmLogoListRecyclerViewAdapter extends RecyclerView.Adapter<AlarmLogoListRecyclerViewAdapter.ViewHolder> {

    private final List<Integer> mValues;
    private Context context;
    private int focusedItem = 0;

    public AlarmLogoListRecyclerViewAdapter(Context context,List<Integer> items) {
        mValues = items;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarmlogo_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.alarmlogo.setImageDrawable(context.getResources().getDrawable(mValues.get(position)));
        holder.itemView.setBackgroundColor(focusedItem == position ? Color.GRAY : Color.TRANSPARENT);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView alarmlogo;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            alarmlogo = (ImageView) view.findViewById(R.id.alarmlistitem);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Redraw the old selection and the new
                    notifyItemChanged(focusedItem);
                    focusedItem = getLayoutPosition();
                    notifyItemChanged(focusedItem);
                }
            });
        }
    }

    public int getSelected(){
        return this.focusedItem;
    }

    public void setSelected(int select){
        this.focusedItem=select;
    }
}
