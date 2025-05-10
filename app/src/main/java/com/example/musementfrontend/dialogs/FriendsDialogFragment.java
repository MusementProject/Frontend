package com.example.musementfrontend.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musementfrontend.R;
import com.example.musementfrontend.dto.FriendDTO;
import com.example.musementfrontend.util.FriendsAdapter;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;

public class FriendsDialogFragment extends DialogFragment {

    private FriendsAdapter adapter;
    private List<FriendDTO> friends = new ArrayList<>();

    public void setFriends(List<FriendDTO> friends){
        this.friends = friends;
        if (adapter != null){
            adapter.updateFriends(friends);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_friends, null);

        RecyclerView recyclerView = view.findViewById(R.id.friendsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FriendsAdapter(friends);
        recyclerView.setAdapter(adapter);

        AlertDialog dialog = builder
                .setView(view)
                .setTitle("Friends")
                .setNegativeButton("Close", (d, which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);
            }
        });

        return dialog;
    }


}
