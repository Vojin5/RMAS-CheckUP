package com.example.checkup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.checkup.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.LocationRestriction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceTypes;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback ,GoogleMap.OnMarkerClickListener{

    //-----------CONSTANTS------------
    private static final String TAG = "MapsActivity";
    private static final int DEFAULT_ZOOM = 15;
    private static final String KEY = "AIzaSyCQIixnPlJGSN7LYFaK3oekf7SOx12ATtM";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    //---------LOCAL---------
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private boolean locationPermissionGranted = false; //Flag for permissions
    EditText search; //Search Bar
    private ImageView gps; //Go to my location
    private String Address; //Holder for current Addres on display
    private ImageView pickPlaceBtn;
    private ImageView filterButton;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double currentLat = 0;
    private double currentLong = 0;
    private boolean placesClicked = false;
    private int radius = 2500;

    private boolean cafe = true;
    private boolean restaurant = true;
    private boolean night_club = true;
    private boolean bar = true;
    private boolean casino = true;
    private ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Place place = Autocomplete.getPlaceFromIntent(intent);
                        Address = place.getAddress();
                        search.setText(Address);
                        moveCamera(place.getLatLng(),15f,place.getName(),place.getId());
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                    Log.i(TAG, "User canceled autocomplete");
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getLocationPermission(); //Permissions for gps

        fusedLocationProviderClient = (FusedLocationProviderClient) LocationServices.getFusedLocationProviderClient(this);
        Places.initialize(getApplicationContext(),KEY);
        PlacesClient placesClient = Places.createClient(this);

        findViews();

        initListeners();
        hideSoftKeyboard();


    }

    private void findViews()
    {
        search = findViewById(R.id.inputSearch);
        gps = findViewById(R.id.gps);
        pickPlaceBtn = findViewById(R.id.getPlacesButton);
        filterButton = findViewById(R.id.filter_maps);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (locationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setOnMarkerClickListener(this);
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode
                == LOCATION_PERMISSION_REQUEST_CODE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void getDeviceLocation()
    {
        try{
            Task location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        Location currentLocation = (Location) task.getResult();
                        currentLat = currentLocation.getLatitude();
                        currentLong = currentLocation.getLongitude();
                        moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),
                                15f
                        ,"My Location","");
                    }
                }
            });
        }
        catch (SecurityException e)
        {
            Log.d("EXCEPTION :",e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng,float zoom,String title,String snippet)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        if(!title.equals("My Location"))
        {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .snippet(snippet);
            mMap.addMarker(markerOptions);
        }
        hideSoftKeyboard();

    }

    private void initListeners()
    {
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });


        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                startAutocomplete();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAutocomplete();
            }
        });

        pickPlaceBtn.setOnClickListener(new View.OnClickListener() {
            //List<String> types = Arrays.asList("cafe","restaurant","night_club","casino","bar");
            @Override
            public void onClick(View v) {
                if(placesClicked)
                {
                    placesClicked = false;
                    mMap.clear();
                    return;
                }
                placesClicked = true;
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
                        + "?location="
                        + currentLat
                        + "," + currentLong
                        + "&radius="
                        + String.valueOf(radius)
                        + "&type="
                        + "cafe"
                        + "&sensor=true"
                        + "&key="
                        + "AIzaSyCQIixnPlJGSN7LYFaK3oekf7SOx12ATtM";
                if(cafe)
                {
                    new PlaceTask(mMap).execute(url);
                }


                String url2 = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
                        + "?location="
                        + currentLat
                        + "," + currentLong
                        + "&radius="
                        + String.valueOf(radius)
                        + "&type="
                        + "restaurant"
                        + "&sensor=true"
                        + "&key="
                        + "AIzaSyCQIixnPlJGSN7LYFaK3oekf7SOx12ATtM";
                if(restaurant)
                {
                    new PlaceTask(mMap).execute(url2);
                }

                String url3 = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
                        + "?location="
                        + currentLat
                        + "," + currentLong
                        + "&radius="
                        + String.valueOf(radius)
                        + "&type="
                        + "night_club"
                        + "&sensor=true"
                        + "&key="
                        + "AIzaSyCQIixnPlJGSN7LYFaK3oekf7SOx12ATtM";
                if(night_club)
                {
                    new PlaceTask(mMap).execute(url3);
                }

                String url4 = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
                        + "?location="
                        + currentLat
                        + "," + currentLong
                        + "&radius="
                        + String.valueOf(radius)
                        + "&type="
                        + "casino"
                        + "&sensor=true"
                        + "&key="
                        + "AIzaSyCQIixnPlJGSN7LYFaK3oekf7SOx12ATtM";
                if(casino)
                {
                    new PlaceTask(mMap).execute(url4);
                }

                String url5 = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
                        + "?location="
                        + currentLat
                        + "," + currentLong
                        + "&radius="
                        + String.valueOf(radius)
                        + "&type="
                        + "bar"
                        + "&sensor=true"
                        + "&key="
                        + "AIzaSyCQIixnPlJGSN7LYFaK3oekf7SOx12ATtM";
                if(bar)
                {
                    new PlaceTask(mMap).execute(url5);
                }
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(MapsActivity.this);
                dialog.setTitle("Filter Maps");
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.filter_maps_dialog);
                dialog.getWindow().getAttributes().width = ActionBar.LayoutParams.FILL_PARENT;

                CheckBox cafeCheck = dialog.findViewById(R.id.cafe_check);
                CheckBox restaurantCheck = dialog.findViewById(R.id.restaurant_check);
                CheckBox barCheck = dialog.findViewById(R.id.bar_check);
                CheckBox night_clubCheck = dialog.findViewById(R.id.night_club_check);
                CheckBox casinoCheck = dialog.findViewById(R.id.casino_check);
                SeekBar seekbar = dialog.findViewById(R.id.seekbar);
                TextView display = dialog.findViewById(R.id.range_display);
                Button confirm = dialog.findViewById(R.id.confirm_filter);
                Button cancel = dialog.findViewById(R.id.cancel_filter);

                cafeCheck.setChecked(cafe);
                restaurantCheck.setChecked(restaurant);
                barCheck.setChecked(bar);
                night_clubCheck.setChecked(night_club);
                casinoCheck.setChecked(casino);
                seekbar.setProgress(radius);
                display.setText(String.valueOf(radius));

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(cafeCheck.isChecked())
                        {
                            cafe = true;
                        }
                        else{
                            cafe = false;
                        }
                        if(restaurantCheck.isChecked())
                        {
                            restaurant = true;
                        }
                        else{
                            restaurant = false;
                        }
                        if(barCheck.isChecked())
                        {
                            bar = true;
                        }
                        else{
                            bar = false;
                        }
                        if(night_clubCheck.isChecked())
                        {
                            night_club = true;
                        }
                        else{
                            night_club = false;
                        }
                        if(casinoCheck.isChecked())
                        {
                            casino = true;
                        }
                        else{
                            casino = false;
                        }
                        radius = Integer.parseInt(display.getText().toString());
                        dialog.dismiss();
                    }
                });
                seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress = ((int)Math.round(progress/100))*100;
                        seekBar.setProgress(progress);
                        display.setText(progress + "");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });

    }


    private void hideSoftKeyboard()
    {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void startAutocomplete()
    {
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID,Place.Field.ADDRESS, Place.Field.NAME,Place.Field.LAT_LNG);
        List<String> types = Arrays.asList("cafe","restaurant","night_club","casino","bar");
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .setTypesFilter(types)
                .build(this);
        startAutocomplete.launch(intent);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Potvrda");
        builder.setMessage("Da li zelite da dodate ovaj objekat u feed : " + marker.getTitle());

        builder.setPositiveButton("DA", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                DatabaseReference reference = FirebaseDatabase.getInstance("https://checkup-f6ce4-default-rtdb.europe-west1.firebasedatabase.app").getReference();
                reference.child("Places")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                        .child(marker.getSnippet())
                        .setValue(true);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NE", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
        return true;
    }}
