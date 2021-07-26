package com.example.fintal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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
        //Set the custom view to the action bar
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(v);

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