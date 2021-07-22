package com.example.fintal.Fragments;

import android.graphics.Color;
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
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {
    //TAG for debugging
    public static final String TAG = "HomeFragment";

    private TextView tvTotalBalance;
    private TextView tvTotalIncome;
    private TextView tvTotalExpense;

    private PieChart pieChart;
    private List<Register> allExpenses;

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

        //Setup pie chart
        pieChart = getView().findViewById(R.id.piechart);
        allExpenses = new ArrayList<>();
        getAllExpenses();

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

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(0);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterTextSize(24);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private void loadPieChartData() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        //Create hashmap to store total amount of expenses by category for user's registers
        HashMap<String, Float> mapCategories = new HashMap<String, Float>();
        for (Register i : allExpenses) {
            if (mapCategories.get(i.getCategory().get("name")) == null) {
                mapCategories.put(i.getCategory().get("name").toString(), i.getAmount().floatValue());
            } else {
                mapCategories.put(i.getCategory().get("name").toString(), mapCategories.get(i.getCategory().get("name").toString()) + i.getAmount().floatValue());
            }
        }

        for (String i : mapCategories.keySet()) {
            entries.add(new PieEntry(mapCategories.get(i), i));
        }

        ArrayList<Integer> colors = new ArrayList<>();
        for (int color: ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color: ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.animateY(1400, Easing.EaseInOutQuad);
    }

    private void getAllExpenses() {
        //Start query with specified class
        ParseQuery<Register> query = ParseQuery.getQuery(Register.class);
        query.include(Register.KEY_CATEGORY);
        query.whereEqualTo(Register.KEY_TYPE, false);
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        //Start asynchronous call for query
        query.findInBackground(new FindCallback<Register>() {
            @Override
            public void done(List<Register> objects, ParseException e) {
                if (e!= null) {
                    Log.e(TAG, "Issue getting last 10 registers");
                    return;
                }
                allExpenses.clear();
                allExpenses.addAll(objects);
                setupPieChart();
                loadPieChartData();
            }
        });
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