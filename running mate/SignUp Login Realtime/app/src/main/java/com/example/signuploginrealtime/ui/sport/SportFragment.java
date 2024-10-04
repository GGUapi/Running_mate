package com.example.signuploginrealtime.ui.sport;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.signuploginrealtime.R;
import com.example.signuploginrealtime.databinding.FragmentSportBinding;
import com.example.signuploginrealtime.ui.chat.ChatFragment;
import com.example.signuploginrealtime.ui.profile.ProfileFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SportFragment extends Fragment {

    private FragmentSportBinding binding;
    ViewPager2 viewPager;
    ConstraintLayout viewSportDetail;
    Button backBtn;
    private static String ownerName;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSportBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TabLayout tabLayout = binding.tabLayout;
        viewPager = binding.viewPager;
        viewSportDetail = binding.sportDetailView;
        backBtn = binding.backButton;
        backBtn.setOnClickListener(this::hideDetail);
        Button joinButton = binding.joinButton;
        Button deleteButton = binding.deleteButton;
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatFragment.setCurrentReceiverName(ownerName);
                NavController navController = NavHostFragment.findNavController(SportFragment.this);
                navController.navigate(R.id.navigation_chat);
                /*ChatFragment chatFragment = new ChatFragment();

                // 使用 FragmentTransaction 添加或替换 ChatFragment
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.navigation_sport, chatFragment); // 替换为您的容器ID
                transaction.addToBackStack(null);  // 如果您希望用户可以通过按后退按钮返回到 SportFragment，请添加此行
                transaction.commit();*/
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( ExistingSportsFragment.getCurrentSport().getOwnerUserName().equals(ProfileFragment.getCurrentProfileName()) ){
                    DatabaseReference sportsRef = FirebaseDatabase.getInstance().getReference("sports");
                    Query thirdChildQuery = sportsRef.orderByKey().limitToFirst(ExistingSportsFragment.getCurrentPos());

                    thirdChildQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int count = 0;
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                count++;
                                if (count == ExistingSportsFragment.getCurrentPos()) {

                                    childSnapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Toast.makeText(getActivity(), "運動已結束!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast.makeText(getActivity(), "刪除失敗: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // 處理錯誤
                            Toast.makeText(getActivity(), "查詢失敗: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(getActivity(), "您不是活動發起者,無法結束!", Toast.LENGTH_SHORT).show();
                }

            }
        });


        SportPagerAdapter pagerAdapter = new SportPagerAdapter(requireActivity());
        pagerAdapter.setSportFragment(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // 設置每個tab的title
            if (position == 0) {
                tab.setText("發起新運動");
            } else {
                tab.setText("已發起的運動");
            }
        }).attach();

        return root;
    }

    public void showDetail(Sport sport) {
        viewPager.setVisibility(View.GONE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user.getUid() == sport.getOwnerID()) {
//            binding.joinButton.setVisibility(View.GONE);
//        } else {
//            binding.joinButton.setVisibility(View.VISIBLE);
//        }
        binding.sportDetailTitle.setText(sport.getTopic());
        binding.sportOwner.setText(sport.getOwnerUserName());
        binding.sportDetailType.setText(sport.getSport());
        String datetime = sport.getDate() + " " + sport.getTime();
        binding.sportDetailDatetime.setText(datetime);
        binding.sportDetailLocation.setText(sport.getLocation());
        binding.sportDetailPeople.setText(sport.getPeople());
        binding.sportDetailNote.setText(sport.getNote());
        viewSportDetail.setVisibility(View.VISIBLE);
        ownerName = binding.sportOwner.getText().toString();
    }

    public void hideDetail(View view) {
        viewSportDetail.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static String getOwnerName(){return ownerName;}
}