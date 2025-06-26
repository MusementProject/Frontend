package com.example.musementfrontend.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musementfrontend.R;
import com.example.musementfrontend.dto.FriendDTO;

import java.util.ArrayList;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    public interface OnProfileClickListener {
        void onProfileClick(FriendDTO friend);
    }

    private final List<FriendDTO> friends;

    private final OnProfileClickListener profileClickListener;

    public FriendsAdapter(List<FriendDTO> friends, OnProfileClickListener listener) {
        this.friends = new ArrayList<>(friends);
        this.profileClickListener = listener;
    }

    public void updateFriends(List<FriendDTO> newFriends) {
        friends.clear();
        friends.addAll(newFriends);
        notifyDataSetChanged();
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageButton profilePicture;
        TextView nickname, username;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.profilePicture);
            nickname = itemView.findViewById(R.id.nickname);
            username = itemView.findViewById(R.id.username);
        }
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        FriendDTO friend = friends.get(position);
        holder.nickname.setText(friend.getNickname());
        holder.username.setText(friend.getUsername());
        String profilePicture = Util.getProfilePhotoDefault();
        if (friend.getProfilePicture() != null) {
            profilePicture = friend.getProfilePicture();
        }

        Glide.with(holder.profilePicture.getContext())
                .load(profilePicture)
                .circleCrop()
                .into(holder.profilePicture);

        holder.profilePicture.setOnClickListener(v -> {
            if (profileClickListener != null){
                profileClickListener.onProfileClick(friend);
            }
        });
    }
    @Override
    public int getItemCount() {
        return friends.size();
    }
}
