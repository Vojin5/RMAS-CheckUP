package com.example.checkup;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class HomeFragment extends Fragment {
    private static final String KEY = "AIzaSyCQIixnPlJGSN7LYFaK3oekf7SOx12ATtM";

    List<Place> places = new ArrayList<>();
    PlaceAdapter adapter;
    RecyclerView recyclerView;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recycler_home);
        adapter = new PlaceAdapter(getContext(),places);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.requireContext()));

        List placeFields = Arrays.asList(Place.Field.NAME,
                Place.Field.ICON_URL,
                Place.Field.ADDRESS,
                Place.Field.CURRENT_OPENING_HOURS,
                Place.Field.ICON_BACKGROUND_COLOR,
                Place.Field.BUSINESS_STATUS,
                Place.Field.PHONE_NUMBER,
                Place.Field.RATING,
                Place.Field.PHOTO_METADATAS,
                Place.Field.PRICE_LEVEL,
                Place.Field.OPENING_HOURS,
                Place.Field.UTC_OFFSET);

        Places.initialize(getContext(),KEY);
        PlacesClient placesClient = Places.createClient(getContext());

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://checkup-f6ce4-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference reference = firebaseDatabase.getReference();
        reference.child("Places")
                .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        FetchPlaceRequest request = FetchPlaceRequest.newInstance(snapshot.getKey(),placeFields);
                        placesClient.fetchPlace(request).addOnSuccessListener((response) ->
                        {
                            Place place = response.getPlace();
                            int position = places.size();
                            places.add(place);
                            adapter.notifyItemInserted(position);
                        });
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        return  view;
    }

}