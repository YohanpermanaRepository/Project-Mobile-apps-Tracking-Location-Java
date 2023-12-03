package com.example.yohanprojekbarulocation;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MainActivity extends AppCompatActivity {

    MapController mapController;
    EditText etLat,etLong;
    MapView mapapp;
    Button btnGo;
    Double currentLat = 0.0, currentLong = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AccessPermission();

        etLat = (EditText) findViewById(R.id.Lat);
        etLong = (EditText) findViewById(R.id.Long);
        btnGo = (Button) findViewById(R.id.buttonGo);
        mapapp = (MapView) findViewById(R.id.mapView);

        mapapp.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        mapapp.setBuiltInZoomControls(true);
        mapapp.setMultiTouchControls(true);
        mapController = (MapController) mapapp.getController();
        mapController.setZoom(15);

        //Mengatur posisi awal map
        GeoPoint pointcenter = new GeoPoint(-7.128866051198645, 112.72587634189512);

        mapController.setCenter(pointcenter);

        final MyLocationNewOverlay myLocationNewOverlay =
                new MyLocationNewOverlay(new GpsMyLocationProvider(this),mapapp);
        myLocationNewOverlay.enableMyLocation();
        mapapp.getOverlays().add(myLocationNewOverlay);
        myLocationNewOverlay.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                if (myLocationNewOverlay.getMyLocation()!=null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mapapp.getController().animateTo(myLocationNewOverlay.getMyLocation());
                            currentLat = myLocationNewOverlay.getMyLocation().getLatitude();
                            currentLong = myLocationNewOverlay.getMyLocation().getLongitude();
                        }
                    });
                }

            }
        });

        //membuat marker pada peta
        GeoPoint mPoint = new GeoPoint(-7.158266, 112.781787);
        Marker myMarkerPoint = new Marker(mapapp);
        myMarkerPoint.setPosition(mPoint);
        myMarkerPoint.setTitle("Suramadu");
        myMarkerPoint.setIcon(this.getResources().getDrawable(R.mipmap.ic_launcher_round));
        myMarkerPoint.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
        mapapp.getOverlays().add(myMarkerPoint);

        MapEventsReceiver mapEventsReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                Toast.makeText(getApplicationContext(),
                        p.getLatitude()+"_"+p.getLongitude(),Toast.LENGTH_SHORT).show();
                etLat.setText(String.valueOf (p.getLatitude()));
                etLong.setText(String.valueOf(p.getLongitude()));

                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        MapEventsOverlay eventsOverlay = new MapEventsOverlay(getApplicationContext(),mapEventsReceiver);
        mapapp.getOverlays().add(eventsOverlay);

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MainActivity2.class);
                intent.putExtra("LatTujuan",Double.parseDouble(etLat.getText().toString()));
                intent.putExtra("LongTujuan",Double.parseDouble(etLong.getText().toString()));
                intent.putExtra("LatAsal",(currentLat));
                intent.putExtra("LongAsal",(currentLong));
                startActivity(intent);

            }
        });
    }

    //membuat selfpermission
    void AccessPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1001);
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH &&
                checkSelfPermission(Manifest.permission.INTERNET)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.INTERNET},
                    1002);
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH &&
                checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                    1003);
        }
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH &&
//                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                    1004);
//        }
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH &&
//                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED){
//            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    1005);
//        }
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1001:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(),
                            "Permission Granted Fine Location",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Permission Denied Fine Location",Toast.LENGTH_SHORT).show();
                }
                break;
            case 1002:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(),
                            "Permission Granted Access Internet",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Permission Denied Access Internet",Toast.LENGTH_SHORT).show();
                }
                break;
            case 1003:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(),
                            "Permission Granted Access Network State",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Permission Denied Access Network State",Toast.LENGTH_SHORT).show();
                }
                break;
//            case 1004:
//                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
//                    Toast.makeText(getApplicationContext(),
//                            "Permission Granted Read External Storage",Toast.LENGTH_SHORT).show();
//                }else{
//                    Toast.makeText(getApplicationContext(),
//                            "Permission Denied Read External Storage",Toast.LENGTH_SHORT).show();
//                }
//                break;
//            case 1005:
//                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
//                    Toast.makeText(getApplicationContext(),
//                            "Permission Granted Write External Storage",Toast.LENGTH_SHORT).show();
//                }else{
//                    Toast.makeText(getApplicationContext(),
//                            "Permission Denied Write External Storage",Toast.LENGTH_SHORT).show();
//                }
//                break;

        }
    }
}