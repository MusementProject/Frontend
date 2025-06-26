package com.example.musementfrontend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.dto.ConcertDTO;
import com.example.musementfrontend.pojo.Concert;
import com.example.musementfrontend.util.Util;
import com.example.musementfrontend.util.UtilFeed;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishlistConcertsFragment extends Fragment {
    private LinearLayout feed;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_concerts, container, false);
        feed = view.findViewById(R.id.feed);
        loadWishlistConcerts();
        return view;
    }

    private void loadWishlistConcerts() {
        if (getArguments() == null) {
            Toast.makeText(getContext(), "Error: No user data", Toast.LENGTH_LONG).show();
            return;
        }
        
        String accessToken = getArguments().getString("accessToken");
        long userId = getArguments().getLong("userId", 0L);
        
        if (accessToken == null || userId == 0) {
            Toast.makeText(getContext(), "Error: User data not found", Toast.LENGTH_LONG).show();
            return;
        }

        APIService apiService = APIClient.getClient().create(APIService.class);
        apiService.getWishlistConcerts("Bearer " + accessToken, userId).enqueue(new Callback<List<ConcertDTO>>() {
            @Override
            public void onResponse(Call<List<ConcertDTO>> call, Response<List<ConcertDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ConcertDTO> concertDTOs = response.body();
                    List<Concert> concerts = UtilFeed.convertConcertDTOsToConcerts(concertDTOs);
                    UtilFeed.FillWishlistConcertsForFragment((AppCompatActivity) requireActivity(), concerts, feed);
                } else {
                    Toast.makeText(getContext(), "Error loading concerts: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<ConcertDTO>> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
} 