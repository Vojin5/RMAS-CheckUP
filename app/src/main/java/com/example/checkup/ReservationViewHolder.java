package com.example.checkup;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.model.Place;

import java.util.List;

public class ReservationViewHolder extends RecyclerView.ViewHolder {
    Context context;
    List<Place> places;
    List<String> times;
    ReservationAdapter adapter;
    ImageView icon;
    TextView timeTxt;

    TextView nameTxt;

    public ReservationViewHolder(@NonNull View itemView, Context context, List<Place> places,List<String> times, ReservationAdapter adapter) {
        super(itemView);
        this.context = context;
        this.places = places;
        this.adapter = adapter;
        this.times = times;

        icon = itemView.findViewById(R.id.icon);
        timeTxt = itemView.findViewById(R.id.reservation);
        nameTxt = itemView.findViewById(R.id.reservationNameTxt);
    }
}
