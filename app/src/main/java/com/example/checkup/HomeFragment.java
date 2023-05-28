package com.example.checkup;

import static android.app.Activity.RESULT_OK;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


public class HomeFragment extends Fragment {
    private static final String KEY = "AIzaSyCQIixnPlJGSN7LYFaK3oekf7SOx12ATtM";
    public static final int PICK_IMAGE = 3;

    List<Place> places = new ArrayList<>();
    int selectedLocation = -1;
    PlaceAdapter adapter;
    RecyclerView recyclerView;
    FloatingActionButton addPhotoButton;

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
        addPhotoButton = view.findViewById(R.id.floating_add_photo);
        adapter = new PlaceAdapter(getContext(),places);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.requireContext()));

        List placeFields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
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
                            if(place == null)
                            {
                                Toast.makeText(getContext(), "Error : place cannot be loaded", Toast.LENGTH_SHORT).show();
                                return;
                            }
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

        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.setTitle("Add photo");
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.add_photo_dialog);
                dialog.getWindow().getAttributes().width = ActionBar.LayoutParams.FILL_PARENT;

                Spinner spinner = dialog.findViewById(R.id.photo_spinner);
                Button addButton = dialog.findViewById(R.id.add_photo_button);
                Button cancel = dialog.findViewById(R.id.cancel_photo_dialog);

                ArrayList<String> names = new ArrayList<>();
                for (Place p : places)
                {
                    names.add(p.getName());
                }
                ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_dropdown_item,names);
                spinner.setAdapter(adapter1);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedLocation = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(selectedLocation == -1)
                        {
                            Toast.makeText(getContext(), "Select one place", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        addPhoto();
                        dialog.dismiss();
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
        return  view;
    }

    private void addPhoto()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            Uri imageURI = data.getData();
            if (imageURI == null) {
                Toast.makeText(getActivity(), "Error adding photo", Toast.LENGTH_SHORT).show();
                return;
            }
            StorageReference storage = FirebaseStorage.getInstance().getReference();
            String uniqueID = UUID.randomUUID().toString();

            storage.child(places.get(selectedLocation).getId())
                    .child(uniqueID)
                    .putFile(imageURI);

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://checkup-f6ce4-default-rtdb.europe-west1.firebasedatabase.app");
            DatabaseReference reference = firebaseDatabase.getReference();
            reference.child("Points")
                    .child("Photos")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                    .child(uniqueID).setValue(true);

            reference.child("Photos")
                    .child(places.get(selectedLocation).getId())
                    .push().setValue(uniqueID);
        }
    }
}