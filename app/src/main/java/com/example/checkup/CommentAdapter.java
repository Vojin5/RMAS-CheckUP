package com.example.checkup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {
    Context context;
    List<String> names;
    List<String> comments;

    public CommentAdapter(Context context, List<String> names, List<String> comments) {
        this.context = context;
        this.names = names;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item_layout,parent,false);
        return new CommentViewHolder(view,context,names,comments,this);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.Name.setText(names.get(position).toString());
        holder.Comment.setText(comments.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return names.size();
    }
}
