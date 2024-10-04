package com.example.signuploginrealtime.ui.sport;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.signuploginrealtime.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class ExistingSportsFragment extends Fragment implements ExistingSportRecyclerViewAdapter.OnItemClickListener {
    private String LOG_TAG = "ExistingSportsFragmentDebug";
    private int mColumnCount = 1;
    static private int currentPos;
    static private Sport currentSport;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    SportFragment parentFragment;
    public ExistingSportsFragment(SportFragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    ExistingSportRecyclerViewAdapter adapter;
    private List<Sport> sports = new ArrayList<Sport>();
    private DatabaseReference db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_existing_sports_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter = new ExistingSportRecyclerViewAdapter(sports, this);
            recyclerView.setAdapter(adapter);
            setupSportsDataOnChange();
        }
        return view;
    }

    private void setupSportsDataOnChange() {
        db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference sportsRef = FirebaseDatabase.getInstance().getReference().child("sports");
        sportsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Sport> newSports = new ArrayList<Sport>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Sport sport = snapshot.getValue(Sport.class);
                    Log.d(LOG_TAG, sport.getSport());
                    newSports.add(sport);
                }
                sports = newSports;
                adapter.setItemList(sports);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(LOG_TAG, "onCancelled");
                Log.d(LOG_TAG, error.getMessage());
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Sport sport = sports.get(position);
        currentPos = position+1;
        currentSport = sport;
        parentFragment.showDetail(sport);
    }

    static public int getCurrentPos(){return currentPos;}

    static public Sport getCurrentSport(){return currentSport;}
}