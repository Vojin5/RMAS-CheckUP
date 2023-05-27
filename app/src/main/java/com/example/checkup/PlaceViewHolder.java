package com.example.checkup;

import android.content.Context;
import android.media.Image;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.model.Place;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlaceViewHolder extends RecyclerView.ViewHolder {
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
    }
}
