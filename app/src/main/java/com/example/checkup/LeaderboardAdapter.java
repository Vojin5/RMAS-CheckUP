package com.example.checkup;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardViewHolder> {
    Context context;
    List<String> names;
    List<Integer> points;

    public LeaderboardAdapter(Context context, List<String> names, List<Integer> points) {
        this.context = context;
        this.names = names;
        this.points = points;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reservation_item_layout,parent,false);
        return new LeaderboardViewHolder(view,context,names,points,this);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        holder.Name.setText(names.get(position));
        holder.Points.setText(points.get(position).toString());
        StorageReference reference = FirebaseStorage.getInstance().getReference();
        reference.child("ProfileImages")
                .child(names.get(position) + ".jpg")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(context).load(uri).into(holder.profileIMG);
                    }
                });

    }

    @Override
    public int getItemCount() {
        return names.size();
    }
}
