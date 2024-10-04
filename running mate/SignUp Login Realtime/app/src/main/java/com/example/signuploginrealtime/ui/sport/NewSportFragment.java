package com.example.signuploginrealtime.ui.sport;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.signuploginrealtime.HelpClass;
import com.example.signuploginrealtime.R;
import com.example.signuploginrealtime.UserMainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class NewSportFragment extends Fragment {
    static String LOG_TAG = "NewSportFragmentDebug";

    public NewSportFragment(SportFragment parentFragment) {
        // Required empty public constructor
        this.parentFragment = parentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    EditText launchTopic, launchSports, launchDate, launchTime,  launchLocation,launchPeople, launchNote;
    Button launchButton;

    SportFragment parentFragment;
    UserMainActivity mainActivity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_newsport, container, false);
        launchTopic = view.findViewById(R.id.launch_topic);
        launchSports = view.findViewById(R.id.launch_sports);
        launchDate = view.findViewById(R.id.launch_date);
        launchTime = view.findViewById(R.id.launch_time);
        launchLocation = view.findViewById(R.id.launch_location);
        launchPeople = view.findViewById(R.id.launch_people);
        launchNote = view.findViewById(R.id.launch_note);
        launchButton = view.findViewById(R.id.launch_button);

        setupCurrentTime();

        launchDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        launchTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        launchButton.setOnClickListener(this::onClickLaunch);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof UserMainActivity) {
            Log.d(LOG_TAG, "set UserMainActivity");
            mainActivity = (UserMainActivity) context;
        }
    }

    private void setupCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String currentDate = year + "-" + (month + 1) + "-" + dayOfMonth;
        launchDate.setText(currentDate);

        String currentTime = hour + ":" + minute;
        launchTime.setText(currentTime);
    }

    private void hideInputWindow() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void onClickLaunch(View view) {
        hideInputWindow();
        Context context = requireContext();
        if (!validateInputs()) {
            Toast.makeText(context, "除了備註之外，欄位不得為空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mainActivity != null)
            mainActivity.showLoading();

        Sport newSport = new Sport(launchTopic.getText().toString(), launchSports.getText().toString(),
                launchDate.getText().toString(), launchTime.getText().toString(),
                launchLocation.getText().toString(), launchPeople.getText().toString(),
                launchNote.getText().toString());

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference sportRefInList = db.child("sports");
        String sportId = sportRefInList.push().getKey();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 取得已註冊成功的使用者
        String userId = user.getUid();
        newSport.setOwner(userId, HelpClass.getInstance().getProfile().getUsername());
        DatabaseReference sportsInUserRef = db.child("users").child(userId).child("sports").child(sportId);
        cleanInputs();
        sportsInUserRef.setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            // 只新增一個ID，透過ID取得 Realtime Database 設定的 sports/$sport_id 的讀寫所有權，再寫入實際內容到 sports/$sport_id
            // 這樣 sports 列表才能讓其他人也讀得到，但就只有創建人可以寫入跟刪除
            @Override
            public void onSuccess(Void unused) {
                Log.d(LOG_TAG, "新增運動成功 STEP1");
                DatabaseReference realSportRef = sportRefInList.child(sportId);
                realSportRef.setValue(newSport).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(LOG_TAG, "新增運動成功 STEP2");
//                        Toast.makeText(context, "新增運動成功", Toast.LENGTH_SHORT).show();
                        if (mainActivity != null)
                            mainActivity.hideLoading();
                        showSportDetail(newSport);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(LOG_TAG, "新增運動失敗 STEP2");
                        Toast.makeText(context, "新增運動失敗", Toast.LENGTH_SHORT).show();
                        if (mainActivity != null)
                            mainActivity.hideLoading();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(LOG_TAG, "新增運動失敗 STEP1");
                Toast.makeText(context, "新增運動失敗", Toast.LENGTH_SHORT).show();
                if (mainActivity != null)
                    mainActivity.hideLoading();
            }
        });
    }

    private boolean validateInputs() {
        return !launchTopic.getText().toString().isEmpty() &&
                !launchDate.getText().toString().isEmpty() &&
                !launchTime.getText().toString().isEmpty() &&
                !launchLocation.getText().toString().isEmpty() &&
                !launchPeople.getText().toString().isEmpty();
    }
    private void cleanInputs() {
        launchTopic.setText("");
        launchSports.setText("");
        launchLocation.setText("");
        launchPeople.setText("");
        launchNote.setText("");
    }

    private void showDatePickerDialog() {
        // 获取当前日期
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // 创建日期选择器对话框
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                        // 在日期选择器对话框中选择日期后执行的逻辑
                        // 将选中的日期设置给 EditText 或执行其他操作
                        String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDayOfMonth;
                        launchDate.setText(selectedDate);
                    }
                }, year, month, dayOfMonth);

        // 显示日期选择器对话框
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        // 取得現在的時間
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // 创建时间选择器对话框
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // 在时间选择器对话框中选择时间后执行的逻辑
                        // 将选中的时间设置给 EditText 或执行其他操作
                        String selectedTime = hourOfDay + ":" + minute;
                        launchTime.setText(selectedTime);
                    }
                }, hour, minute, true);

        // 显示时间选择器对话框
        timePickerDialog.show();
    }

    private void showSportDetail(Sport sport) {
        if (parentFragment != null)
            parentFragment.showDetail(sport);
        else
            Log.d(LOG_TAG, "parentFragment is null");
    }
}