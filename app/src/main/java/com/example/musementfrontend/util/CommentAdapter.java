package com.example.musementfrontend.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musementfrontend.R;
import com.example.musementfrontend.pojo.Comment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> comments;

    public CommentAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);

        holder.tvAuthor.setText(comment.getUser().getNickname());
        holder.tvCommentText.setText(comment.getMessage());

        Date date = comment.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        String formattedDate = formatter.format(date);

        holder.tvDate.setText(formattedDate);

        String profilePicture = Util.getProfilePhotoDefault();
        if (comment.getUser().getProfilePicture() != null) {
            profilePicture = comment.getUser().getProfilePicture();
        }

        Glide.with(holder.ivUserPhoto.getContext())
                .load(profilePicture)
                .circleCrop()
                .into(holder.ivUserPhoto);
    }

    @Override
    public int getItemCount() {
        return comments != null ? comments.size() : 0;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        ImageView ivUserPhoto;
        TextView tvAuthor;
        TextView tvDate;
        TextView tvCommentText;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserPhoto = itemView.findViewById(R.id.ivUserPhoto);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvCommentText = itemView.findViewById(R.id.tvCommentText);
        }
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        notifyItemInserted(comments.size() - 1);
    }
}
