package com.example.musementfrontend.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.Friend;
import com.example.musementfrontend.Login;
import com.example.musementfrontend.Profile;
import com.example.musementfrontend.R;
import com.example.musementfrontend.dto.FriendDTO;
import com.example.musementfrontend.dto.User;
import com.example.musementfrontend.util.FriendsAdapter;
import com.example.musementfrontend.util.IntentKeys;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsDialogFragment extends DialogFragment {

    private FriendsAdapter adapter;
    private List<FriendDTO> friends = new ArrayList<>();
    private User user;

    public void setFriends(List<FriendDTO> friends) {
        this.friends = friends;
        if (adapter != null) {
            adapter.updateFriends(friends);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            this.user = args.getParcelable(IntentKeys.getUSER(), User.class);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_friends, null);
        RecyclerView recyclerView = view.findViewById(R.id.friendsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FriendsAdapter(friends, friend -> {
            dismiss();
            Intent intent = new Intent(requireContext(), Friend.class);
            intent.putExtra(IntentKeys.getUSER(), user);
            intent.putExtra(IntentKeys.getFRIEND_USERNAME(), friend.getUsername());
            startActivity(intent);
        });
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

        Button friendsButton = view.findViewById(R.id.friendsButton);
        friendsButton.setOnClickListener(this::OnClickFriendsButton);

        Button followersButton = view.findViewById(R.id.followersButton);
        followersButton.setOnClickListener(this::OnClickFollowersButton);

        Button followingButton = view.findViewById(R.id.followingButton);
        followingButton.setOnClickListener(this::OnClickFollowingButton);

        OnClickFriendsButton(null);
        return dialog;
    }

    public void OnClickFriendsButton(View view) {
        APIService apiService = APIClient.getClient().create(APIService.class);
        Call<List<FriendDTO>> call = apiService.getAllUserFriends("Bearer " + user.getAccessToken(), user.getId());
        call.enqueue(new Callback<List<FriendDTO>>() {
            @Override
            public void onResponse(Call<List<FriendDTO>> call, Response<List<FriendDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FriendDTO> friendList = response.body();
                    setFriends(friendList);
                }
            }

            @Override
            public void onFailure(Call<List<FriendDTO>> call, Throwable t) {
                Log.e("Friends", "API call failed", t);
            }
        });
    }

    public void OnClickFollowersButton(View view) {
        APIService apiService = APIClient.getClient().create(APIService.class);
        Call<List<FriendDTO>> call = apiService.getAllUserFollowers("Bearer " + user.getAccessToken(), user.getId());
        call.enqueue(new Callback<List<FriendDTO>>() {
            @Override
            public void onResponse(Call<List<FriendDTO>> call, Response<List<FriendDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FriendDTO> friendList = response.body();
                    setFriends(friendList);
                }
            }

            @Override
            public void onFailure(Call<List<FriendDTO>> call, Throwable t) {
                Log.e("Friends", "API call failed", t);
            }
        });
    }

    public void OnClickFollowingButton(View view) {
        APIService apiService = APIClient.getClient().create(APIService.class);
        Call<List<FriendDTO>> call = apiService.getAllUserFollowing("Bearer " + user.getAccessToken(), user.getId());
        call.enqueue(new Callback<List<FriendDTO>>() {
            @Override
            public void onResponse(Call<List<FriendDTO>> call, Response<List<FriendDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FriendDTO> friendList = response.body();
                    setFriends(friendList);
                }
            }

            @Override
            public void onFailure(Call<List<FriendDTO>> call, Throwable t) {
                Log.e("Friends", "API call failed", t);
            }
        });
    }

}
