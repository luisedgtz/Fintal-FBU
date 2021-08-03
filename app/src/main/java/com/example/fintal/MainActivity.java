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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static Integer selectedMonth;
    public static Integer selectedYear;
    private TextView tvDateSelected;

    Fragment fragment;
    Fragment fragment1;
    Fragment fragment2;
    Fragment fragment3;

    String fragmentTag = "HomeFragment";

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
        ImageButton btnAccount = v.findViewById(R.id.btnAccount);
        //Get Layout View for date filter
        ConstraintLayout clDate = v.findViewById(R.id.filterDate);
        //Get text view for display date filter
        tvDateSelected = v.findViewById(R.id.tvDateAB);
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
        //Set on click listener to Image Button to launch AccountsActivity
        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AccountsActivity.class);
                startActivity(i);
            }
        });
        final Calendar today = Calendar.getInstance();
        //Define MonthPickerDialog
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(MainActivity.this,
                new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) {
                        changeDate(selectedMonth, selectedYear);
                    }
                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));
        //Set on click listener to month/year selector
        clDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setActivatedMonth(Calendar.AUGUST)
                        .setMinYear(1990)
                        .setActivatedYear(2021)
                        .setMaxYear(2030)
                        .setMinMonth(Calendar.JANUARY)
                        .setTitle("Select trading month")
                        .setMonthRange(Calendar.JANUARY, Calendar.DECEMBER)
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
        fragment1 = new HomeFragment();
        fragment2 = new IncomeFragment();
        fragment3 = new ExpenseFragment();

        // Define bottom tab navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        //Set default selected item
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        fragment = fragment1;
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment, fragmentTag).commit();

        //Navigation item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_expense:
                        fragment = fragment3;
                        fragmentTag = "ExpenseFragment";
                        break;
                    case R.id.action_income:
                        fragment = fragment2;
                        fragmentTag = "IncomeFragment";
                        break;
                    case R.id.action_home:
                    default:
                        fragment = fragment1;
                        fragmentTag = "HomeFragment";
                        break;
                }

                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment, fragmentTag).commit();
                return true;
            }
        });
    }

    private void changeDate(int selectedMonth, int selectedYear) {
        this.selectedMonth = selectedMonth;
        this.selectedYear = selectedYear;
        //Get string for month
        String monthString = new DateFormatSymbols().getMonths()[selectedMonth];
        //Set text for Text View filter date
        tvDateSelected.setText(monthString + ", " + Integer.toString(selectedYear));

        if (fragment.getTag() == "HomeFragment") {
            Log.d(TAG, "Home");
            HomeFragment fragmentHome = (HomeFragment)fragment1;
            fragmentHome.getLastRegisters();
            fragmentHome.getBalance();
            fragmentHome.getAllExpenses();
        } else if (fragment.getTag() == "IncomeFragment") {
            Log.d(TAG, "Income");
            IncomeFragment fragmentIncome = (IncomeFragment) fragment2;
            fragmentIncome.getIncomes();
        } else if (fragment.getTag() == "ExpenseFragment") {
            Log.d(TAG, "Expense");
            ExpenseFragment fragmentExpense = (ExpenseFragment) fragment3;
            fragmentExpense.getExpenses();
        }
    }
}

