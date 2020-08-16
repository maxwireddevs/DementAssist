package com.wireddevs.dementassist.Utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.wireddevs.dementassist.R;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> itemListName=new ArrayList<>();
    private ArrayList<Integer> itemList=new ArrayList<>();

    // Constructor
    public ImageAdapter(Context c) {
        mContext = c;

        itemListName.add("Home");
        itemListName.add("Maps");
        itemListName.add("Alarm");
        itemListName.add("Contacts");
        itemListName.add("Settings");
        itemListName.add("About");

        itemList.add(R.drawable.ic_home_black_24dp);
        itemList.add(R.drawable.ic_directions_black_24dp);
        itemList.add(R.drawable.ic_notifications_black_24dp);
        itemList.add(R.drawable.ic_people_black_24dp);
        itemList.add(R.drawable.ic_build_black_24dp);
        itemList.add(R.drawable.ic_import_contacts_black_24dp);
    }

    public int getCount() {
        return itemList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        ImageView logo;
        TextView pronunciation;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null){
            grid = new View(mContext);
            grid = inflater.inflate(R.layout.home_griditem, null);
            logo = (ImageView) grid.findViewById(R.id.logo_homeitem);
            pronunciation = (TextView)grid.findViewById(R.id.text_homeitem);
        }
        else{
            grid = (View) convertView;
            logo = (ImageView) grid.findViewById(R.id.logo_homeitem);
            pronunciation = (TextView)grid.findViewById(R.id.text_homeitem);
        }

        logo.setImageDrawable(mContext.getResources().getDrawable(itemList.get(position)));
        pronunciation.setText(itemListName.get(position));

        return grid;
    }
}