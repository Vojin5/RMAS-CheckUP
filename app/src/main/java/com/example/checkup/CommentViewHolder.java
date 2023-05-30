package com.example.checkup;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Comment;

import java.util.List;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    Context context;
    List<String> names;
    List<String> comments;
    CommentAdapter adapter;
    TextView Name;
    TextView Comment;


    public CommentViewHolder(@NonNull View itemView,Context context,List<String> names,List<String> comments,CommentAdapter adapter) {
        super(itemView);
        this.context = context;
        this.adapter = adapter;
        this.names = names;
        this.comments = comments;

        Name = itemView.findViewById(R.id.comment_name);
        Comment = itemView.findViewById(R.id.comment_text);


    }
}
