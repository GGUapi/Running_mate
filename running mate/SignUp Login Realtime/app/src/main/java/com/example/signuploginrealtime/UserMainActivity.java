package com.example.signuploginrealtime;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class UserMainActivity extends AppCompatActivity {
    static String LOG_TAG = "UserMainDebug";
    private BottomNavigationView bottomNavigationView;
    private NavController navController;

    MaterialToolbar toolbar;

    View loadingView;
    ViewGroup rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_main);
        toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setTitleCentered(true);
        bottomNavigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_profile, R.id.navigation_map, R.id.navigation_chat,R.id.navigation_sport)
                .build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_user_main);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        toolbar.setNavigationOnClickListener(this::Logout);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                if (navDestination.getId() == R.id.navigation_profile) {
                    Log.d(LOG_TAG, "個人資料");
                    setTitle(R.string.title_profile);
                } else if (navDestination.getId() == R.id.navigation_map) {
                    Log.d(LOG_TAG, "地圖");
                    setTitle(R.string.title_map);
                } else if (navDestination.getId() == R.id.navigation_chat) {
                    Log.d(LOG_TAG, "聊天室");
                    setTitle(R.string.title_chat);
                } else if (navDestination.getId() == R.id.navigation_sport) {
                    Log.d(LOG_TAG, "運動");
                    setTitle(R.string.title_sport);
                }
            }
        });

        rootView = findViewById(android.R.id.content);
        loadingView = LayoutInflater.from(this).inflate(R.layout.layout_loading, rootView, false);
    }

    private void Logout(View v) {
        Log.d(LOG_TAG, "Logout");
        FirebaseAuth.getInstance().signOut();
        finish();
    }

    public void showLoading() {
        if (rootView.indexOfChild(loadingView) == -1)
            rootView.addView(loadingView);
    }

    public void hideLoading() {
        if (rootView.indexOfChild(loadingView) != -1)
            rootView.removeView(loadingView);
    }

}