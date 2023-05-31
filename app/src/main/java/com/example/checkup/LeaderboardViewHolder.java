package com.example.checkup;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LeaderboardViewHolder extends RecyclerView.ViewHolder {
    Context context;
    List<String> names;
    List<Integer> points;
    LeaderboardAdapter adapter;
    ImageView profileIMG;
    TextView Name;
    TextView Points;


    public LeaderboardViewHolder(@NonNull View itemView,Context context,List<String> names,List<Integer> points,LeaderboardAdapter adapter) {
        super(itemView);
        this.context = context;
        this.names = names;
        this.points = points;
        this.adapter = adapter;

        profileIMG = itemView.findViewById(R.id.icon);
        Name = itemView.findViewById(R.id.reservationNameTxt);
        Points = itemView.findViewById(R.id.reservation);
    }
}
