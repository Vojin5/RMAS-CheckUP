package com.example.checkup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

import java.util.List;
import java.util.Random;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceViewHolder> {
    private static final String KEY = "AIzaSyCQIixnPlJGSN7LYFaK3oekf7SOx12ATtM";
    Context context;
    List<Place> places;

    public PlaceAdapter(Context context, List<Place> places) {
        this.context = context;
        this.places = places;
    }


    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.place_card,parent,false);
        return new PlaceViewHolder(view,context,this,places);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        Place place = places.get(position);
        Places.initialize(context,KEY);
        PlacesClient placesClient = Places.createClient(context);
        System.out.println(place.getName());
        holder.placeName.setText(place.getName());
        holder.placeAddress.setText(place.getAddress());
        try {
            int openingHour = place.getOpeningHours().getPeriods().get(0).getOpen().getTime().getHours();
            int closingHour = place.getOpeningHours().getPeriods().get(0).getClose().getTime().getHours();
            String openTime = "";
            String closeTime = "";
            if (openingHour < 10) {
                openTime = "0" + String.valueOf(openingHour);
            } else {
                openTime = String.valueOf(openingHour);
            }
            if (closingHour < 10) {
                closeTime = "0" + String.valueOf(closingHour);
            } else {
                closeTime = String.valueOf(closingHour);
            }
            holder.placeOpenHours.setText("Open : " + openTime + " - " + closeTime);
        }
        catch (Exception e)
        {
            holder.placeOpenHours.setText("not found");
        }
        if(place.isOpen() != null && place.isOpen())
        {
            holder.placeOpen.setText("Open");
        }
        else{
            holder.placeOpen.setText("Closed");
        }
        Random random = new Random();
        if(random.nextInt(1000) % 2 == 0)
        {
            holder.placeAvailable.setImageDrawable(new ColorDrawable(Color.GREEN));
            holder.available = true;
        }
        else{
            holder.placeAvailable.setImageDrawable(new ColorDrawable(Color.RED));
            holder.available = false;
        }

        if(place.getIconUrl() == null || place.getIconUrl().equals(""))
        {
            holder.placeIcon.setImageResource(R.drawable.coffee);
        }
        else{
            Glide.with(context).load(place.getIconUrl()).into(holder.placeIcon);
        }
        holder.placeIcon.setBackgroundColor(place.getIconBackgroundColor());

        try{
            PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);
            FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata).build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
                @Override
                public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    holder.placeImage.setImageBitmap(bitmap);
                }
            });
        }
        catch (Exception e)
        {
            holder.placeImage.setImageResource(R.drawable.no_photo);
        }
        int price;
        if(place.getPriceLevel() == null)
        {
            price = 1;
        }
        else{
            price = place.getPriceLevel();
        }
        System.out.println(price);
        switch (price)
        {
            case 1:
                holder.dollar2.setAlpha(0.5f);
                holder.dollar3.setAlpha(0.5f);
                break;
            case 2:
                holder.dollar3.setAlpha(0.5f);
                break;
        }
        holder.googleRating.setText(place.getRating().toString());

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://checkup-f6ce4-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference reference = firebaseDatabase.getReference();
        reference.child("Ratings")
                .child(places.get(position).getId())
                .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                .get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            holder.userRating.setText(dataSnapshot.getValue(String.class));
                        }
                        else {
                            holder.userRating.setText("Rate place");
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.userRating.setText("/");
                    }
                });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }
}
