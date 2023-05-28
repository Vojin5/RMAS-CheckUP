package com.example.checkup;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlaceViewHolder extends RecyclerView.ViewHolder {
    private static final String KEY = "AIzaSyCQIixnPlJGSN7LYFaK3oekf7SOx12ATtM";

    Context context;
    List<Place> places;
    PlaceAdapter adapter;
    TextView placeName;
    TextView placeAddress;
    TextView placeOpenHours;
    TextView placeOpen;
    ImageView placeImage;
    ImageView placeIcon;
    CircleImageView placeAvailable;
    Button placeReserve;
    ImageView placeDelete;
    ImageView dollar1;
    ImageView dollar2;
    ImageView dollar3;
    Button addRatingButton;
    TextView googleRating;
    TextView userRating;
    List<String> userImages = new ArrayList<>();

    int photoIterator = 0;
    boolean loaded = false;


    public PlaceViewHolder(@NonNull View itemView,Context context,PlaceAdapter adapter,List<Place> places) {
        super(itemView);
        this.places = places;
        this.adapter = adapter;
        this.context = context;

        placeName = itemView.findViewById(R.id.placeName);
        placeAddress = itemView.findViewById(R.id.placeAddress);
        placeOpenHours = itemView.findViewById(R.id.placeOpenHours);
        placeOpen = itemView.findViewById(R.id.placeOpen);
        placeImage = itemView.findViewById(R.id.placePhoto);
        placeAvailable = itemView.findViewById(R.id.placeAvailable);
        placeReserve = itemView.findViewById(R.id.placeReserveButton);
        placeIcon = itemView.findViewById(R.id.placeIcon);
        placeDelete = itemView.findViewById(R.id.placeDelete);
        dollar1 = itemView.findViewById(R.id.dollar_one);
        dollar2 = itemView.findViewById(R.id.dollar_two);
        dollar3 = itemView.findViewById(R.id.dollar_three);
        addRatingButton = itemView.findViewById(R.id.addRating);
        googleRating = itemView.findViewById(R.id.google_rating);
        userRating = itemView.findViewById(R.id.userRate);

        addRatingButton.setOnClickListener(this::addRating);
        placeDelete.setOnClickListener(this::deletePlace);
        placeImage.setOnClickListener(this::changeImage);

    }

    private void changeImage(View view)
    {
        Places.initialize(context,KEY);
        PlacesClient placesClient = Places.createClient(context);
        StorageReference storage = FirebaseStorage.getInstance().getReference();
        photoIterator++;
        System.out.println(photoIterator);


        if(photoIterator == 1 && !loaded)
        {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://checkup-f6ce4-default-rtdb.europe-west1.firebasedatabase.app/");
            DatabaseReference reference = firebaseDatabase.getReference();
            reference.child("Photos")
                    .child(places.get(getAdapterPosition()).getId())
                    .get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                                {
                                    userImages.add(snapshot.getValue(String.class));
                                }
                                loaded = true;
                            }
                        }
                    }).addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            System.out.println("size: "+userImages.size());
                            if(photoIterator <= userImages.size())
                            {
                                storage.child(places.get(getAdapterPosition()).getId())
                                        .child(userImages.get(photoIterator-1))
                                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Glide.with(context).load(uri).into(placeImage);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                System.out.println(e.getMessage());
                                            }
                                        });
                            }
                        }
                    });
        }else{
            System.out.println("size: "+userImages.size());
            if(photoIterator <= userImages.size())
            {
                storage.child(places.get(getAdapterPosition()).getId())
                        .child(userImages.get(photoIterator-1))
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(context).load(uri).into(placeImage);
                            }
                        });
            }
            else {
                photoIterator = 0;
                try{
                    PhotoMetadata photoMetadata = places.get(getAdapterPosition()).getPhotoMetadatas().get(0);
                    FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata).build();
                    placesClient.fetchPhoto(photoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
                        @Override
                        public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                            Bitmap bitmap = fetchPhotoResponse.getBitmap();
                            placeImage.setImageBitmap(bitmap);
                        }
                    });
                }
                catch (Exception e)
                {
                    placeImage.setImageResource(R.drawable.no_photo);
                }

            }
        }

    }


    private void addRating(View view)
    {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://checkup-f6ce4-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference reference = firebaseDatabase.getReference();

        Dialog dialog = new Dialog(context);
        dialog.setTitle("Rate a place");
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.rating_dialog);
        dialog.getWindow().getAttributes().width = ActionBar.LayoutParams.FILL_PARENT;

        ImageView star1 = dialog.findViewById(R.id.star1);
        ImageView star2 = dialog.findViewById(R.id.star2);
        ImageView star3 = dialog.findViewById(R.id.star3);
        ImageView star4 = dialog.findViewById(R.id.star4);
        ImageView star5 = dialog.findViewById(R.id.star5);
        TextView name = dialog.findViewById(R.id.placeName);
        Button rateBtn = dialog.findViewById(R.id.rateButton);
        Button cancel = dialog.findViewById(R.id.cancelButton);
        TextView stars = dialog.findViewById(R.id.stars);

        name.setText(places.get(getAdapterPosition()).getName());
        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setImageResource(R.drawable.filled_star);
                star2.setImageResource(R.drawable.empty_star);
                star3.setImageResource(R.drawable.empty_star);
                star4.setImageResource(R.drawable.empty_star);
                star5.setImageResource(R.drawable.empty_star);
                stars.setText("1");
            }
        });
        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setImageResource(R.drawable.filled_star);
                star2.setImageResource(R.drawable.filled_star);
                star3.setImageResource(R.drawable.empty_star);
                star4.setImageResource(R.drawable.empty_star);
                star5.setImageResource(R.drawable.empty_star);
                stars.setText("2");
            }
        });
        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setImageResource(R.drawable.filled_star);
                star2.setImageResource(R.drawable.filled_star);
                star3.setImageResource(R.drawable.filled_star);
                star4.setImageResource(R.drawable.empty_star);
                star5.setImageResource(R.drawable.empty_star);
                stars.setText("3");
            }
        });
        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setImageResource(R.drawable.filled_star);
                star2.setImageResource(R.drawable.filled_star);
                star3.setImageResource(R.drawable.filled_star);
                star4.setImageResource(R.drawable.filled_star);
                star5.setImageResource(R.drawable.empty_star);
                stars.setText("4");
            }
        });
        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setImageResource(R.drawable.filled_star);
                star2.setImageResource(R.drawable.filled_star);
                star3.setImageResource(R.drawable.filled_star);
                star4.setImageResource(R.drawable.filled_star);
                star5.setImageResource(R.drawable.filled_star);
                stars.setText("5");
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stars.getText().equals("0"))
                {
                    Toast.makeText(context, "Click on the star", Toast.LENGTH_SHORT).show();
                    return;
                }
                reference.child("Ratings")
                        .child(places.get(getAdapterPosition()).getId())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                        .setValue(stars.getText().toString());
                reference.child("Points")
                         .child("Ratings")
                         .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                         .child(places.get(getAdapterPosition()).getId()).setValue(stars.getText().toString());
                userRating.setText(stars.getText().toString());
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void deletePlace(View view)
    {
        int position = getAdapterPosition();
        if(position == RecyclerView.NO_POSITION)
        {
            return;
        }
        Place place = places.get(position);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://checkup-f6ce4-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference reference = firebaseDatabase.getReference();
        reference.child("Places")
                .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                .child(place.getId())
                .removeValue();
        places.remove(position);
        adapter.notifyItemRemoved(position);
    }
}
