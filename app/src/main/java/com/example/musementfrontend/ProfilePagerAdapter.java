package com.example.musementfrontend;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ProfilePagerAdapter extends FragmentStateAdapter {
    private String accessToken;
    private long userId;

    public ProfilePagerAdapter(@NonNull FragmentActivity fragmentActivity, String accessToken, long userId) {
        super(fragmentActivity);
        this.accessToken = accessToken;
        this.userId = userId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new AttendingConcertsFragment();
                break;
            case 1:
                fragment = new WishlistConcertsFragment();
                break;
            default:
                fragment = new AttendingConcertsFragment();
                break;
        }
        
        Bundle args = new Bundle();
        args.putString("accessToken", accessToken);
        args.putLong("userId", userId);
        fragment.setArguments(args);
        
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
} 