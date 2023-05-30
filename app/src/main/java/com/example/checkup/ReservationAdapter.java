package com.example.checkup;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.libraries.places.api.model.Place;

import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationViewHolder> {
    Context context;
    List<Place> places;
    List<String> times;

    public ReservationAdapter(Context context, List<Place> places,List<String> times) {
        this.context = context;
        this.places = places;
        this.times = times;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reservation_item_layout,parent,false);
        return new ReservationViewHolder(view,context,places,times,this);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        holder.timeTxt.setText(times.get(position));
        Uri uri = Uri.parse(places.get(position).getIconUrl());
        Glide.with(context).load(uri).into(holder.icon);
        holder.nameTxt.setText(places.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return places.size();
    }
}
