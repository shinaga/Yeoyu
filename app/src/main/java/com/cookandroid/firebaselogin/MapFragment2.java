package com.cookandroid.firebaselogin;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashSet;
import java.util.List;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment2#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class MapFragment2 extends Fragment implements OnMapReadyCallback, PlacesListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment2.
     */
    // TODO: Rename and change types and number of parameters

    public MapFragment2(Context context,HomeActivity h) {
       this.context = context;
       this.h = h;
    }
    Context context;
    HomeActivity h;
    private MapView googlemap = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map2, container, false);

        googlemap = view.findViewById(R.id.map);
        googlemap.getMapAsync(this);

        Button button_cafe = (Button)view.findViewById(R.id.button_cafe);
        button_cafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlace_Cafe();
            }
        });
        return view;
    }
    public void showPlace_Cafe()
    {
        new NRPlaces.Builder()
                .listener((PlacesListener) context)
                .key("AIzaSyCTqlWqciTWTl6lHhxN2e_-Jx6xK11jlD0")
                .latlng(37.6326283, 127.0267038)//현재 위치
                .radius(1000) //1km 내에서 검색
                .type(PlaceType.CAFE) //카페
                .build()
                .execute();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if(googlemap!=null){
            googlemap.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        googlemap.onStart();

    }


    @Override
    public void onStop() {
        googlemap.onStop();
        super.onStop();

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

    }

    @Override
    public void onPlacesFailure(PlacesException e) {

    }

    @Override
    public void onPlacesStart() {

    }

    @Override
    public void onPlacesSuccess(List<Place> places) {

    }

    @Override
    public void onPlacesFinished() {

    }
}