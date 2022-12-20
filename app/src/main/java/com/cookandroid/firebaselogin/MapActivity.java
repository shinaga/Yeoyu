package com.cookandroid.firebaselogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;


public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,GoogleMap.OnMarkerClickListener,PlacesListener{
    //테스트!!!!!!!!

    private GoogleMap mMap;
    private Marker currentMarker = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초
    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;


    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소


    Location mCurrentLocatiion;
    LatLng currentPosition;


    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;


    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.
    // (참고로 Toast에서는 Context가 필요했습니다.)
    private double latitude;//마커 클릭시 변경되는 위도 값
    private double longitude;//마커 클릭시 변경되는 경도 값

    private LinearLayout linear;

    private Button button_cafe;
    private Button button_rest;
    private Button button_conv;
    private Button button_hearth;

    View marker_root_view;//커스텀 마거 뷰
    TextView tv_marker;//마커에 있는 텍스트뷰
    TextView tv_marker_check;//마커에 있는 텍스트뷰
    Marker marker;

    @Override//뒤로가기 이벤트
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if(keycode ==KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(this,HomeActivity.class);
            startActivity(intent);
            return true;
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_map);

        mLayout = findViewById(R.id.layout_main);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        previous_marker = new ArrayList<Marker>();
        setCustomMarkerView();//커스텀 마커
    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {   CameraUpdate center = CameraUpdateFactory.newLatLng(marker.getPosition());
        mMap.animateCamera(center);


        this.marker = marker;
        marker.setTitle(marker.getTitle());
        marker.showInfoWindow();
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        button_hearth.setVisibility(View.VISIBLE);//이때부터 하트버튼 클릭 가능

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference map = database.getReference("map");
        map.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                String location = latitude+" "+longitude;
                String loc = "";
                for(char c : location.toCharArray()){// 점'.'이 파이어베이스 문자열로 못들어 가기 떄문에 '-'로 변경
                    if(c=='.'){
                        loc += "-";
                    }
                    else loc += c;
                }
                int heartCount = 0;//하트 개수를 담을 변수
                if(snapshot.child(loc).child("heartCount").getValue()!=null) {//하트를 아무도 안눌렀으면 발동하지 않음
                    heartCount = Integer.parseInt(snapshot.child(loc).child("heartCount").getValue()+"");
                }

                String id = MainActivity.editId.getText().toString();//id
                if(snapshot.child(loc).child(id).getValue()==null){//좋아요를 누른적이 없으면
                    button_hearth.setText(heartCount+" \uD83E\uDD0D");
                }
                else if((boolean)snapshot.child(loc).child(id).getValue()==false){//좋아요가 안눌러져 있으면
                    button_hearth.setText(heartCount+" \uD83E\uDD0D");
                }
                else{
                    button_hearth.setText(heartCount+" \uD83E\uDDE1");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return true;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");

        mMap = googleMap;

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();
        MarkerOptions markerOptions = new MarkerOptions();
        mMap.setOnMarkerClickListener(this);

        /*
        //본관
        LatLng Main = new LatLng(37.58475463956453, 126.9250272363605 );
        markerOptions.position(Main);
        mnarkerOptions.title("명지전문대 본관");
        markerOptios.snippet("여기에 정보 입력");
        mMap.addMarker(markerOptions);
        //공학관
        LatLng Engine = new LatLng(37.585099197178224, 126.92491367259198 );
        markerOptions.position(Engine);
        markerOptions.title("명지전문대 공학관");
        markerOptions.snippet("여기에 정보 입력");
        mMap.addMarker(markerOptions);
        */


        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            startLocationUpdates(); // 3. 위치 업데이트 시작


        }else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions( MapActivity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }



        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 현재 오동작을 해서 주석처리

        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                Log.d( TAG, "onMapClick :");
            }
        });

    }

    private void setCustomMarkerView() {
        marker_root_view = LayoutInflater.from(this).inflate(R.layout.marker, null);
        tv_marker = (TextView) marker_root_view.findViewById(R.id.tv_marker);
        tv_marker_check = (TextView) marker_root_view.findViewById(R.id.tv_marker_check);
    }
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);

                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());


                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "onLocationResult : " + markerSnippet);


                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);

                mCurrentLocatiion = location;
            }


        }

    };



    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);



            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mMap.setMyLocationEnabled(true);

        }

    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap!=null)
                mMap.setMyLocationEnabled(true);

        }


    }


    @Override
    protected void onStop() {

        super.onStop();

        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }




    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {

            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {


        if (currentMarker != null) currentMarker.remove();


        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        markerOptions.draggable(true);


        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentPosition, 15);
        mMap.animateCamera(cameraUpdate);

        //여기 다가 클릭 이벤트를 지정하는 이유는 액티비티 실행하자마자 바로 클릭하면 오류가 뜨기 때문
        linear = findViewById(R.id.linear);
        linear.setVisibility(View.VISIBLE);
        //카페 찾기
        button_cafe = (Button)findViewById(R.id.button_cafe);
        button_rest = (Button)findViewById(R.id.button_rest);
        button_conv = (Button)findViewById(R.id.button_conv);
        button_hearth = (Button)findViewById(R.id.button_hearth);
        button_cafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_cafe.setTextColor(Color.parseColor("#F5F5F5"));
                button_rest.setTextColor(Color.parseColor("#84837D"));
                button_conv.setTextColor(Color.parseColor("#84837D"));
                button_cafe.setBackgroundResource(R.drawable.search_button);
                button_rest.setBackgroundResource(R.drawable.roundb1);
                button_conv.setBackgroundResource(R.drawable.roundb1);
                Toast.makeText(MapActivity.this,"카페를 찾고 있습니다. 잠시만 기다려 주세요.",Toast.LENGTH_SHORT).show();
                showPlace_Cafe(currentPosition);
            }
        });
        //식당 찾기
        button_rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_cafe.setTextColor(Color.parseColor("#84837D"));
                button_rest.setTextColor(Color.parseColor("#F5F5F5"));
                button_conv.setTextColor(Color.parseColor("#84837D"));
                button_cafe.setBackgroundResource(R.drawable.roundb1);
                button_rest.setBackgroundResource(R.drawable.search_button);
                button_conv.setBackgroundResource(R.drawable.roundb1);
                Toast.makeText(MapActivity.this,"식당을 찾고 있습니다. 잠시만 기다려 주세요.",Toast.LENGTH_SHORT).show();
                showPlace_Res(currentPosition);
            }
        });
        //편의점 찾기
        button_conv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_cafe.setTextColor(Color.parseColor("#84837D"));
                button_rest.setTextColor(Color.parseColor("#84837D"));
                button_conv.setTextColor(Color.parseColor("#F5F5F5"));
                button_cafe.setBackgroundResource(R.drawable.roundb1);
                button_rest.setBackgroundResource(R.drawable.roundb1);
                button_conv.setBackgroundResource(R.drawable.search_button);
                Toast.makeText(MapActivity.this,"편의점을 찾고 있습니다. 잠시만 기다려 주세요.",Toast.LENGTH_SHORT).show();
                showPlace_Conv(currentPosition);
            }
        });
        //하트 버튼 클릭시
        button_hearth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference map = database.getReference("map");
                map.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String location = latitude+" "+longitude;
                        String loc = "";
                        for(char c : location.toCharArray()){// 점'.'이 파이어베이스 문자열로 못들어 가기 떄문에 '-'로 변경
                            if(c=='.'){
                                loc += "-";
                            }
                            else loc += c;
                        }

                        int heartCount = 0;//하트 개수를 담을 변수
                        if(snapshot.child(loc).child("heartCount").getValue()!=null) {//하트를 아무도 안눌렀으면 발동하지 않음
                            heartCount = Integer.parseInt(snapshot.child(loc).child("heartCount").getValue()+"");
                        }

                        String id = MainActivity.editId.getText().toString();//id
                        if(snapshot.child(loc).child(id).getValue()==null){//좋아요를 누른적이 없으면
                            map.child(loc).child(id).setValue(true);
                            map.child(loc).child("heartCount").setValue(++heartCount+"");
                            button_hearth.setText(heartCount+" \uD83E\uDDE1");

                            tv_marker.setVisibility(View.GONE);
                            tv_marker_check.setVisibility(View.VISIBLE);
                        }
                        else if((boolean)snapshot.child(loc).child(id).getValue()==false){//좋아요가 안눌러져 있으면
                            map.child(loc).child(id).setValue(true);
                            map.child(loc).child("heartCount").setValue(++heartCount+"");
                            button_hearth.setText(heartCount+" \uD83E\uDDE1");

                            tv_marker.setVisibility(View.GONE);
                            tv_marker_check.setVisibility(View.VISIBLE);
                        }
                        else{
                            map.child(loc).child(id).setValue(false);
                            map.child(loc).child("heartCount").setValue(--heartCount+"");
                            button_hearth.setText(heartCount+" \uD83E\uDD0D");

                            tv_marker.setVisibility(View.VISIBLE);
                            tv_marker_check.setVisibility(View.GONE);
                        }
                        tv_marker.setText(heartCount+"");
                        tv_marker_check.setText(heartCount+"");
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapActivity.this, marker_root_view)));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

    }


    public void setDefaultLocation() {


        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 여부 확인하세요";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        currentMarker = mMap.addMarker(markerOptions);

        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        //mMap.animateCamera(cameraUpdate);

    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }

        return false;

    }



    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {


                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                } else {


                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");


                        needRequest = true;

                        return;
                    }
                }
                break;
        }
    }
    List<Marker> previous_marker = null;

    public void markerReset()
    {
        mMap.clear();//지도 클리어

        if (previous_marker != null)
            previous_marker.clear();//지역정보 마커 클리어
    }

    public void showPlace_Cafe(LatLng location)
    {
        markerReset();

        new NRPlaces.Builder()
                .listener(MapActivity.this)
                .key("AIzaSyCTqlWqciTWTl6lHhxN2e_-Jx6xK11jlD0")
                .latlng(location.latitude, location.longitude)//현재 위치
                .radius(1000) //1km 내에서 검색
                .type(PlaceType.CAFE) //카페
                .build()
                .execute();
    }
    public void showPlace_Res(LatLng location)
    {
        markerReset();

        new NRPlaces.Builder()
                .listener(MapActivity.this)
                .key("AIzaSyCTqlWqciTWTl6lHhxN2e_-Jx6xK11jlD0")
                .latlng(location.latitude, location.longitude)//현재 위치
                .radius(1000) //1km 내에서 검색
                .type(PlaceType.RESTAURANT) //음식점
                .build()
                .execute();
    }
    public void showPlace_Conv(LatLng location)
    {
        markerReset();

        new NRPlaces.Builder()
                .listener(MapActivity.this)
                .key("AIzaSyCTqlWqciTWTl6lHhxN2e_-Jx6xK11jlD0")
                .latlng(location.latitude, location.longitude)//현재 위치
                .radius(1000) //1km 내에서 검색
                .type(PlaceType.CONVENIENCE_STORE) //편의점
                .build()
                .execute();
    }




    @Override
    public void onPlacesFailure(PlacesException e) {

    }

    @Override
    public void onPlacesStart() {

    }
    @Override
    public void onPlacesSuccess(final List<Place> places) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (noman.googleplaces.Place place : places) {


                    LatLng latLng
                            = new LatLng(place.getLatitude()
                            , place.getLongitude());
                    String markerSnippet = getCurrentAddress(latLng);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(place.getName());
                    markerOptions.snippet(markerSnippet);
                    //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference map = database.getReference("map");
                    map.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            String location = place.getLatitude()+" "+place.getLongitude();
                            String loc = "";
                            for(char c : location.toCharArray()){// 점'.'이 파이어베이스 문자열로 못들어 가기 떄문에 '-'로 변경
                                if(c=='.'){
                                    loc += "-";
                                }
                                else loc += c;
                            }
                            String id = MainActivity.editId.getText().toString();//id
                            if(snapshot.child(loc).child(id).getValue()==null){//좋아요를 누른적이 없으면
                                tv_marker.setVisibility(View.VISIBLE);
                                tv_marker_check.setVisibility(View.GONE);
                            }
                            else if((boolean)snapshot.child(loc).child(id).getValue()==false){//좋아요가 안눌러져 있으면
                                tv_marker.setVisibility(View.VISIBLE);
                                tv_marker_check.setVisibility(View.GONE);
                            }
                            else{
                                tv_marker.setVisibility(View.GONE);
                                tv_marker_check.setVisibility(View.VISIBLE);
                            }
                            int heartCount = 0;//하트 개수를 담을 변수
                            if(snapshot.child(loc).child("heartCount").getValue()!=null) {//하트를 아무도 안눌렀으면 발동하지 않음
                                heartCount = Integer.parseInt(snapshot.child(loc).child("heartCount").getValue()+"");
                            }
                            tv_marker.setText(heartCount+"");
                            tv_marker_check.setText(heartCount+"");
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapActivity.this, marker_root_view)));//커스텀 마커
                            Marker item = mMap.addMarker(markerOptions);
                            previous_marker.add(item);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                //중복 마커 제거
                HashSet<Marker> hashSet = new HashSet<Marker>();
                hashSet.addAll(previous_marker);
                previous_marker.clear();
                previous_marker.addAll(hashSet);

            }
        });

    }

    // View를 Bitmap으로 변환
    private Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }
    @Override
    public void onPlacesFinished() {

    }


}