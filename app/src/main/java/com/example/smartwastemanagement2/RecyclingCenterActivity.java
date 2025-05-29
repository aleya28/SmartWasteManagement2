package com.example.smartwastemanagement2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.List;

public class RecyclingCenterActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 100;
    private EditText editSearch;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycling_center);

        // Back button logic
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(RecyclingCenterActivity.this, HomepageActivity.class);
            startActivity(intent);
            finish(); // optional
        });

        // Init Places SDK
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyA5yfoWimJAeVDt_MrdjUZBSGyV4NRjMXQ");
        }

        // Find EditText for custom search bar
        editSearch = findViewById(R.id.editSearch);

        // On click open Google autocomplete
        editSearch.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });

        // Load Map Fragment
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mapContainer, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);

        // Init Bottom Navigation
        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home); // highlight Home icon

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomepageActivity.class));
                return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, ChatActivity.class));
                return true;
            } else if (id == R.id.nav_booking) {
                startActivity(new Intent(this, BookingActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Recycling Center
        LatLng center1 = new LatLng(3.1390, 101.6869); // KL
        LatLng center2 = new LatLng(3.0738, 101.5183); // Gombak
        LatLng center3 = new LatLng(3.0847, 101.5495); // Setapak
        LatLng center4 = new LatLng(3.1578, 101.7113); // Ampang

        // Markers
        mMap.addMarker(new MarkerOptions().position(center1).title("Recycling Center - KL"));
        mMap.addMarker(new MarkerOptions().position(center2).title("Recycling Center - Gombak"));
        mMap.addMarker(new MarkerOptions().position(center3).title("Recycling Center - Setapak"));
        mMap.addMarker(new MarkerOptions().position(center4).title("Recycling Center - Ampang"));

        // Fokus ke lokasi utama (KL)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center1, 12f));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("DEBUG", "onActivityResult triggered"); // Debug: method called

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.d("DEBUG", "Place selected: " + place.getName() + ", Location: " + place.getLatLng());

                if (mMap != null && place.getLatLng() != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15f));
                    mMap.addMarker(new MarkerOptions()
                            .position(place.getLatLng())
                            .title(place.getName()));

                    editSearch.setText(place.getName());
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR && data != null) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e("PLACES_ERROR", "Autocomplete error: " + status.getStatusMessage());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("Places Error", "User canceled autocomplete or unknown error");
            }
        }
    }
}
