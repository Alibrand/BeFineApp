package com.ksacp2022t3.befine;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.befine.databinding.ActivitySelectLocationBinding;
import com.ksacp2022t3.befine.models.Account;

import java.util.Arrays;

public class NearbyMedicalStuffActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivitySelectLocationBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    LatLng current_location;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_medical_stuff);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient= LocationServices.getFusedLocationProviderClient(this);
        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("Loading");
                progressDialog.setMessage("Please Wait");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        load_medical_stuff();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(NearbyMedicalStuffActivity.this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION  }, 100);

            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                current_location=new LatLng(21.4932675,39.2391473);
                if(location!=null)
                    current_location=new LatLng(location.getLatitude(),location.getLongitude());
                // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                CameraUpdate cameraUpdate=CameraUpdateFactory.newLatLngZoom(current_location,15f);
                mMap.moveCamera(cameraUpdate);
            }
        });

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                String title=marker.getTitle();
                String uid=marker.getTag().toString();
                if(title.startsWith("Doctor"))
                {
                    Intent intent = new Intent(NearbyMedicalStuffActivity.this,DoctorProfileActivity. class);
                    intent.putExtra("uid",uid);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(NearbyMedicalStuffActivity.this,PharmacyProfileActivity. class);
                    intent.putExtra("uid",uid);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100)
        {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                makeText(NearbyMedicalStuffActivity.this,"We could not determine your location" , LENGTH_LONG).show();
            }

        }
    }

    void  load_medical_stuff(){
        progressDialog.show();
        firestore.collection("accounts")
                .whereIn("type", Arrays.asList("Doctor","Pharmacy"))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();
                        for (DocumentSnapshot doc:queryDocumentSnapshots.getDocuments()
                             ) {
                            Account account=doc.toObject(Account.class);
                            LatLng latLng=new LatLng(account.getLocation().getLatitude(),
                                    account.getLocation().getLongitude());
                            int height = 140;
                            int width = 105;
                            if(account.getType().equals("Doctor"))
                            {
                                BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.clinic_pin);
                                Bitmap b = bitmapdraw.getBitmap();
                                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                                Marker marker=mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title("Doctor:"+account.getFirst_name()+" "+account.getLast_name()));
                                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                                        marker.setTag(account.getId());
                            }
                            else
                            {
                                BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.pharma_pin);
                                Bitmap b = bitmapdraw.getBitmap();
                                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                                Marker marker=mMap.addMarker(new MarkerOptions()
                                                .position(latLng)
                                                .title("Pharmacy:"+account.getPharmacy_name()));
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                                marker.setTag(account.getId());

                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                         Toast.makeText(NearbyMedicalStuffActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                    }
                });
    }
}