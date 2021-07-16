package com.example.fintal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.fintal.Fragments.ExpenseFragment;
import com.example.fintal.Fragments.HomeFragment;
import com.example.fintal.Fragments.IncomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting custom action bar
        ActionBar actionBar = getSupportActionBar();
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.actionbar, null);
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