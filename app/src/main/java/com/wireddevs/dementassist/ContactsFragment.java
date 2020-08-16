package com.wireddevs.dementassist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wireddevs.dementassist.Contacts.Contacts;
import com.wireddevs.dementassist.Contacts.ContactsHelper;
import com.wireddevs.dementassist.Utils.RecyclerViewItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ContactsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private MyContactsRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContactsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ContactsFragment newInstance(int columnCount) {
        ContactsFragment fragment = new ContactsFragment();
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
        View view = inflater.inflate(R.layout.fragment_contacts_list, container, false);

        final TextView addtext=new TextView(getActivity());;

        RecyclerView v=view.findViewById(R.id.list);
        final RelativeLayout rl=view.findViewById(R.id.rl);

        final ArrayList<Contacts> contacts=new ArrayList<>();
        ContactsHelper ch=new ContactsHelper(getActivity());
        Cursor res= ch.getAllData();
        while(res.moveToNext()){
            contacts.add(new Contacts(res.getString(0),res.getString(1),res.getString(2)));
        }

        if(contacts.size()==0){
            addtext.setTextSize(20);
            addtext.setTextColor(Color.BLACK);
            addtext.setAlpha((float)0.50);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            addtext.setText("No contacts found");
            addtext.setLayoutParams(layoutParams);
            rl.addView(addtext);
            RelativeLayout.LayoutParams textlayoutparams = (RelativeLayout.LayoutParams)addtext.getLayoutParams();
            textlayoutparams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            addtext.setLayoutParams(textlayoutparams);

            Toast.makeText(getActivity(), "Contacts empty, add contacts using the button.", Toast.LENGTH_SHORT).show();
        }

        adapter=new MyContactsRecyclerViewAdapter(contacts, mListener);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.floatingactionbutton);
        fab.show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add Contacts");
                builder.setCancelable(true);
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view= inflater.inflate(R.layout.contacts_dialog, null);
                builder.setView(view);
                final EditText editname=(EditText)view.findViewById(R.id.nameedittext);
                final EditText editrelations=(EditText)view.findViewById(R.id.relatonshipedittext);
                final EditText editphone=(EditText)view.findViewById(R.id.phoneedittext);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContactsHelper ch=new ContactsHelper(getActivity());
                        ch.insertItem(editname.getText().toString(),editrelations.getText().toString(),editphone.getText().toString());
                        contacts.add(new Contacts(editname.getText().toString(),editrelations.getText().toString(),editphone.getText().toString()));
                        adapter.notifyDataSetChanged();
                        addtext.setVisibility(View.INVISIBLE);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });



        // Set the adapter
        if (v instanceof RecyclerView) {
            Context context = v.getContext();
            RecyclerView recyclerView = (RecyclerView) v;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            recyclerView.setAdapter(adapter);
            recyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getActivity(), recyclerView, new RecyclerViewItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel: "+contacts.get(position).getPhonenum()));
                    startActivity(intent);
                }
                @Override
                public void onLongItemClick(View view, final int position) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Edit Contacts");
                    builder.setCancelable(true);
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View view2= inflater.inflate(R.layout.contacts_dialog, null);
                    builder.setView(view2);
                    final EditText editname=(EditText)view2.findViewById(R.id.nameedittext);
                    final EditText editrelations=(EditText)view2.findViewById(R.id.relatonshipedittext);
                    final EditText editphone=(EditText)view2.findViewById(R.id.phoneedittext);

                    editname.setText(contacts.get(position).getName());
                    editrelations.setText(contacts.get(position).getRelations());
                    editphone.setText(contacts.get(position).getPhonenum());

                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ContactsHelper ch=new ContactsHelper(getActivity());
                            ch.editItem(contacts.get(position).getPhonenum(),editname.getText().toString(),editrelations.getText().toString(),editphone.getText().toString());
                            contacts.get(position).setName(editname.getText().toString());
                            contacts.get(position).setRelations(editrelations.getText().toString());
                            contacts.get(position).setPhonenum(editphone.getText().toString());
                            adapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ContactsHelper ch=new ContactsHelper(getActivity());
                            ch.deleteItem(contacts.get(position).getPhonenum());
                            contacts.remove(position);
                            adapter.notifyDataSetChanged();
                            if(contacts.size()==0){
                                addtext.setTextSize(20);
                                addtext.setTextColor(Color.BLACK);
                                addtext.setAlpha((float)0.50);
                                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                addtext.setText("No contacts found");
                                addtext.setLayoutParams(layoutParams);
                                rl.addView(addtext);
                                RelativeLayout.LayoutParams textlayoutparams = (RelativeLayout.LayoutParams)addtext.getLayoutParams();
                                textlayoutparams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                addtext.setLayoutParams(textlayoutparams);
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
        // TODO: Update argument type and name
        void onListFragmentInteraction(Contacts item);
    }
}
