package com.example.signuploginrealtime.ui.sport;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SportPagerAdapter extends FragmentStateAdapter {
    public SportPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    SportFragment sportFragment;

    public void setSportFragment(SportFragment sportFragment) {
        this.sportFragment = sportFragment;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 根据位置返回相应的 Fragment
        if (position == 0) {
            return new NewSportFragment(sportFragment);
        } else {
            return new ExistingSportsFragment(sportFragment);
        }
    }

    @Override
    public int getItemCount() {
        return 2; // 返回分页的数量
    }
}
