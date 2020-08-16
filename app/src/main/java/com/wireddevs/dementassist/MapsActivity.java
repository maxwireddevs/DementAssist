package com.wireddevs.dementassist;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wireddevs.dementassist.Alarm.AlarmHelper;
import com.wireddevs.dementassist.Alarm.AlarmReceiver;
import com.wireddevs.dementassist.Marker.MarkerHelper;
import com.wireddevs.dementassist.Marker.MarkerStore;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MapsActivity extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean locationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 99;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private Location mLastLocation;
    private SupportMapFragment mapFrag;
    private LocationRequest mLocationRequest;
    private SharedPreferences isNav;
    private SharedPreferences destinstore;
    private FusedLocationProviderClient mFusedLocationClient;
    private CameraPosition cameraPosition;
    private String[] markertypes;
    private int markerid=R.drawable.ic_home_black_24dp;
    private int type;
    private MarkerHelper mh;
    private ArrayList<String> markerlist=new ArrayList<>();
    private LatLng destination;
    private String destinname;
    private FloatingActionButton navbutton,quitbutton;
    private TextView navtext;
    private TextView disttext;
    private boolean isnav;
    private int open=1;
    private ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_maps, container, false);
        mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
        mh=new MarkerHelper(getActivity());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (savedInstanceState != null) {
            mLastLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        markertypes=getActivity().getResources().getStringArray(R.array.markertypes);
        navbutton=(FloatingActionButton) view.findViewById(R.id.navbutton);
        quitbutton=(FloatingActionButton) view.findViewById(R.id.quitbutton);
        quitbutton.hide();

        quitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Navigation cancelled.", Toast.LENGTH_SHORT).show();
                AlarmHelper ah=new AlarmHelper(getActivity());
                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                Intent updateServiceIntent = new Intent(getActivity(), AlarmReceiver.class);
                int code=ah.getCode(destinname);
                PendingIntent pendingUpdateIntent = PendingIntent.getBroadcast(getActivity(),code,updateServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingUpdateIntent);
                pendingUpdateIntent.cancel();
                ah.deleteItem(code);
                SharedPreferences.Editor isnavedit=isNav.edit();
                isnavedit.putBoolean("isnav",false);
                isnavedit.apply();
                isnav=false;
                quitbutton.hide();
            }
        });

        navtext=(TextView)view.findViewById(R.id.navtext);
        disttext=(TextView)view.findViewById(R.id.disttext);
        isNav=getActivity().getSharedPreferences("isnav",Context.MODE_PRIVATE);
        destinstore=getActivity().getSharedPreferences("destinname",Context.MODE_PRIVATE);
        destinname=destinstore.getString("destinname","");

        isnav=isNav.getBoolean("isnav",false);
        if(isnav){
            navtext.setVisibility(View.VISIBLE);
            disttext.setVisibility(View.VISIBLE);
            quitbutton.show();
            destination=new LatLng(mh.getV(destinname),mh.getV1(destinname));
            navtext.setText(String.format("Destination: %s",destinname));
        }


        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.floatingactionbutton);
        fab.show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add Places");
                builder.setCancelable(true);
                type=0;
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view= inflater.inflate(R.layout.marker_dialog, null);
                builder.setView(view);

                final EditText editmarkername=(EditText) view.findViewById(R.id.editmarkername);

                builder.setSingleChoiceItems(markertypes, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int n) {
                        switch(n){
                            case 0:
                                type=0;
                                markerid=R.drawable.ic_home_red_24dp;
                                break;
                            case 1:
                                type=1;
                                markerid=R.drawable.ic_grade_black_24dp;
                                break;
                            case 2:
                                type=2;
                                markerid=R.drawable.ic_pin_drop_black_24dp;
                                break;
                        }
                    }
                });
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(editmarkername.getText().toString().length()!=0){
                            mh.insertItem(editmarkername.getText().toString(),mLastLocation.getLatitude(),mLastLocation.getLongitude(),type);
                            MarkerOptions markerOptions = new MarkerOptions();
                            LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                            markerOptions.position(latLng).title(editmarkername.getText().toString()).draggable(true).icon(getMarkerIconFromDrawable(getResources().getDrawable(markerid),editmarkername.getText().toString()));
                            mMap.addMarker(markerOptions).showInfoWindow();
                            markerlist.add(editmarkername.getText().toString());
                            Toast.makeText(getActivity(), "Hold marker to move", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getActivity(), "Name field cannot be empty", Toast.LENGTH_SHORT).show();
                        }
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

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }


    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable, String title) {

        View markerLayout = getLayoutInflater().inflate(R.layout.marker_title, null);

        ImageView markerImage = ((ImageView) markerLayout.findViewById(R.id.markerlogo));
        TextView markerText = ((TextView) markerLayout.findViewById(R.id.markertitle));

        markerImage.setImageDrawable(drawable);
        markerText.setText(title);

        markerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        markerLayout.layout(0, 0, markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight());

        final Bitmap bitmap = Bitmap.createBitmap(markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerLayout.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        Cursor res=mh.getAllData();

        while(res.moveToNext()){
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(res.getDouble(1), res.getDouble(2));
            switch(res.getInt(3)){
                case 0:
                    type=0;
                    markerid=R.drawable.ic_home_red_24dp;
                    break;
                case 1:
                    type=1;
                    markerid=R.drawable.ic_grade_black_24dp;
                    break;
                case 2:
                    type=2;
                    markerid=R.drawable.ic_pin_drop_black_24dp;
                    break;
            }
            markerOptions.position(latLng).title(res.getString(0)).draggable(true).icon(getMarkerIconFromDrawable(getResources().getDrawable(markerid), res.getString(0)));
            mMap.addMarker(markerOptions).showInfoWindow();
            markerlist.add(res.getString(0));
        }

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            String name="";
            @Override
            public void onMarkerDragStart(Marker marker) {
                name=marker.getTitle();
            }
            @Override
            public void onMarkerDrag(Marker marker) {}
            @Override
            public void onMarkerDragEnd(Marker marker) {
                mh.editItem(name,marker.getPosition().latitude,marker.getPosition().longitude);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete Marker");
                builder.setCancelable(true);
                builder.setMessage("Delete this marker?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mh.deleteItem(marker.getTitle());
                        marker.remove();
                        markerlist.remove(marker.getTitle());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                return false;
            }
        });

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(4000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }

        navbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Navigate");
                builder.setCancelable(true);
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view= inflater.inflate(R.layout.nav_dialog, null);
                builder.setView(view);
                ListView lv=(ListView)view.findViewById(R.id.markerlist);
                adapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,markerlist);
                lv.setAdapter(adapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                        try{
                            destination=new LatLng(mh.getV(markerlist.get(position)),mh.getV1(markerlist.get(position)));
                            Random r=new Random();
                            int nextt=r.nextInt(10000);
                            AlarmHelper ah=new AlarmHelper(getActivity());
                            ah.insertItem(markerlist.get(position),Calendar.getInstance().getTimeInMillis(),nextt,1000*60*5,"1111111",R.drawable.ic_directions_black_24dp);
                            Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                            intent.putExtra("requestCode", nextt);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),nextt, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            AlarmManager am = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
                            am.setExact(AlarmManager.RTC,Calendar.getInstance().getTimeInMillis(),pendingIntent);
                            Toast.makeText(getActivity(), "Alarm will be rung every 5 minutes until you reach the destination.", Toast.LENGTH_LONG).show();

                            SharedPreferences.Editor isnavedit=isNav.edit();
                            isnavedit.putBoolean("isnav",true);
                            isnavedit.apply();
                            isnav=true;

                            SharedPreferences.Editor destinedit=destinstore.edit();
                            destinedit.putString("destinname",markerlist.get(position));
                            destinedit.apply();
                            destinname=markerlist.get(position);

                            quitbutton.show();

                            navtext.setVisibility(View.VISIBLE);
                            disttext.setVisibility(View.VISIBLE);
                            navtext.setText(String.format("Destination: %s", markerlist.get(position)));
                            disttext.setText(String.format("Straight distance: %s", round(calculationByDistance(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()),destination), 2)+" KM"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.show();
            }
        });
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if(open==1){
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                    open--;
                }
                if(isnav){
                    double destin=calculationByDistance(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()),destination);
                    disttext.setText(String.format("Straight distance: %s", round(destin, 2)+" KM"));
                    mMap.addPolyline(new PolylineOptions()
                            .clickable(false)
                            .add(
                                    new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()),
                                    destination
                            ));



                    if(destin<0.1){
                        Toast.makeText(getActivity(), "You have arrived at your destination.", Toast.LENGTH_SHORT).show();
                        AlarmHelper ah=new AlarmHelper(getActivity());
                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                        Intent updateServiceIntent = new Intent(getActivity(), AlarmReceiver.class);
                        int code=ah.getCode(destinname);
                        PendingIntent pendingUpdateIntent = PendingIntent.getBroadcast(getActivity(),code,updateServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.cancel(pendingUpdateIntent);
                        pendingUpdateIntent.cancel();
                        ah.deleteItem(code);
                        SharedPreferences.Editor isnavedit=isNav.edit();
                        isnavedit.putBoolean("isnav",false);
                        isnavedit.apply();
                        isnav=false;
                        quitbutton.hide();
                    }
                }
            }
        }
    };

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private double calculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);
        return Radius * c;
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}