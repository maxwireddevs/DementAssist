package com.wireddevs.dementassist;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.wireddevs.dementassist.Contacts.Contacts;
import com.wireddevs.dementassist.ContactsFragment.OnListFragmentInteractionListener;

import java.util.List;


public class MyContactsRecyclerViewAdapter extends RecyclerView.Adapter<MyContactsRecyclerViewAdapter.ViewHolder> {

    private final List<Contacts> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyContactsRecyclerViewAdapter(List<Contacts> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contacts, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContactsName.setText(String.format("Name: %s", mValues.get(position).getName()));
        holder.mContactsPhone.setText(String.format("Phone Number: %s", mValues.get(position).getPhonenum()));
        holder.mContactsRelations.setText(String.format("Relationship: %s", mValues.get(position).getRelations()));

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
        public final TextView mContactsName;
        public final TextView mContactsRelations;
        public final TextView mContactsPhone;
        public Contacts mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContactsName = (TextView) view.findViewById(R.id.name);
            mContactsRelations = (TextView) view.findViewById(R.id.relations);
            mContactsPhone = (TextView) view.findViewById(R.id.phonenum);
        }
    }
}
