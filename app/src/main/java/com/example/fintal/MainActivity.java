package com.example.fintal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.fintal.Fragments.ExpenseFragment;
import com.example.fintal.Fragments.HomeFragment;
import com.example.fintal.Fragments.IncomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting custom action bar
        ActionBar actionBar = getSupportActionBar();
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.actionbar, null);
        //Get image view for user profile photo
        ImageView iv = v.findViewById(R.id.ivUserPicture);
        //Get Layout View for date filter
        ConstraintLayout clDate = v.findViewById(R.id.filterDate);
        //Get user photo from Parse for current user
        String objectId = ParseUser.getCurrentUser().getObjectId();
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.include("profilePhoto");
        query.whereEqualTo("objectId" , objectId);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                ParseFile imageUser = objects.get(0).getParseFile("profilePhoto");
                if (imageUser != null) {
                    Log.d(TAG, "Setting photo");
                    //Set photo on iv
                    Glide.with(getApplicationContext()).load(imageUser.getUrl()).circleCrop().into(iv);
                }
            }
        });
        //Set on click listener to image view of profile photo to launch UserActivity
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), UserActivity.class);
                startActivity(i);
            }
        });
        final Calendar today = Calendar.getInstance();
        //Define MonthPickerDialog
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(MainActivity.this,
                new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) {

                    }
                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));
        //Set on click listener to month/year selector
        clDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setActivatedMonth(Calendar.JULY)
                        .setMinYear(1990)
                        .setActivatedYear(2017)
                        .setMaxYear(2030)
                        .setMinMonth(Calendar.FEBRUARY)
                        .setTitle("Select trading month")
                        .setMonthRange(Calendar.FEBRUARY, Calendar.NOVEMBER)
                        .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                            @Override
                            public void onMonthChanged(int selectedMonth) {

                            }})
                        .setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                            @Override
                            public void onYearChanged(int selectedYear) {

                            } })
                        .build()
                        .show();
            }
        });
        //Set the custom view to the action bar
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(v, params);

        //FragmentManager
        final FragmentManager fragmentManager = getSupportFragmentManager();
        //Fragments
        final Fragment fragment1 = new HomeFragment();
        final Fragment fragment2 = new IncomeFragment();
        final Fragment fragment3 = new ExpenseFragment();

        // Define bottom tab navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        //Set default selected item
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment1).commit();

        //Navigation item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_expense:
                        fragment = fragment3;
                        break;
                    case R.id.action_income:
                        fragment = fragment2;
                        break;
                    case R.id.action_home:
                    default:
                        fragment = fragment1;
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
    }
}