package com.example.fintal.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fintal.Adapters.RegisterAdapter;
import com.example.fintal.Models.Register;
import com.example.fintal.Models.User;
import com.example.fintal.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    //TAG for debugging
    public static final String TAG = "HomeFragment";

    private TextView tvTotalBalance;
    private TextView tvTotalIncome;
    private TextView tvTotalExpense;

    private RecyclerView rvRegisters;
    protected RegisterAdapter adapter;
    protected List<Register> registers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        rvRegisters = getView().findViewById(R.id.rvRegisters);
        //Initialize array that will hold registers and RegisterAdapter
        registers = new ArrayList<>();
        adapter = new RegisterAdapter(getContext(), registers);
        rvRegisters.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvRegisters.setLayoutManager(linearLayoutManager);

        tvTotalBalance = getView().findViewById(R.id.tvTotalBalance);
        tvTotalIncome = getView().findViewById(R.id.tvIncomes);
        tvTotalExpense = getView().findViewById(R.id.tvExpenses);

        getTotalBalance();
        getLastRegisters();
    }

    private void getLastRegisters() {
        //Start query with specified class
        ParseQuery<Register> query = ParseQuery.getQuery(Register.class);
        //limit to 10 items
        query.setLimit(10);
        query.include(Register.KEY_CATEGORY);
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        //order items from newest to oldest
        query.addDescendingOrder("createdAt");
        //Start asynchronous call for query
        query.findInBackground(new FindCallback<Register>() {
            @Override
            public void done(List<Register> objects, ParseException e) {
                if (e!= null) {
                    Log.e(TAG, "Issue getting last 10 registers");
                    return;
                }
                adapter.clear();
                adapter.addAll(objects);
            }
        });
    }

    private void getTotalBalance() {
        //Get objectId for current logged user
        String objectId = ParseUser.getCurrentUser().getObjectId();
        //Start query
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereEqualTo("objectId" , objectId);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with gettting user" , e);
                    return;
                }
                Number totalIncome = objects.get(0).getNumber("totalIncome");
                Number totalExpenses = objects.get(0).getNumber("totalExpenses");
                Number totalBalance = totalIncome.floatValue() - totalExpenses.floatValue();
                tvTotalBalance.setText("$" + totalBalance.toString());
                tvTotalIncome.setText("$" + totalIncome.toString());
                tvTotalExpense.setText("$" + totalExpenses.toString());
            }
        });

    }
}