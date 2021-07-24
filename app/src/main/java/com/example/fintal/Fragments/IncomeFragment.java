package com.example.fintal.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fintal.Adapters.RegisterAdapter;
import com.example.fintal.Models.Category;
import com.example.fintal.Models.Register;
import com.example.fintal.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class IncomeFragment extends Fragment {
    public static final String TAG = "IncomeFragment";

    private RecyclerView rvIncomes;
    protected RegisterAdapter adapter;
    protected List<Register> incomesList;
    protected FloatingActionButton btnAddIncome;

    List<Category> categories;
    ArrayList<String> categoriesString;
    String selectedCategoryString;
    Category selectedCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_income, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        rvIncomes = getView().findViewById(R.id.rvIncomes);
        //Initialize array that will hold registers and RegisterAdapter
        incomesList = new ArrayList<>();
        adapter = new RegisterAdapter(getContext(), incomesList);
        rvIncomes.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvIncomes.setLayoutManager(linearLayoutManager);

        //Add click listener to floating action button
        btnAddIncome = getView().findViewById(R.id.btnAddIncome);
        btnAddIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewDialog();
            }
        });

        //Initialize variables for categories
        categories = new ArrayList<>();
        categoriesString = new ArrayList<>();
        selectedCategoryString = "";
        getCategories();

        getIncomes();
    }

    //Triggered when floating action button is clicked
    //displays a dialog for user to fill information about new task
    private void showNewDialog() {
        new NewIncomeFragment().show(getChildFragmentManager(), NewIncomeFragment.TAG);
    }

    //Function to get categories
    private void getCategories() {
        ParseQuery<Category> query = ParseQuery.getQuery(Category.class);
        query.addAscendingOrder("createdAt");
        query.findInBackground(new FindCallback<Category>() {
            @Override
            public void done(List<Category> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue getting categories", e);
                    return;
                }
                categories.addAll(objects);
                for (int i = 0; i < categories.size(); i++) {
                    //Add only Name string of each Category object
                    categoriesString.add(categories.get(i).getName());
                }
                setupChipGroup();
            }
        });
    }

    private void setupChipGroup() {
        //Get chipGroup
        ChipGroup chipGroup = getView().findViewById(R.id.chipGroupExpense);
        //Set chips dynamically for all existing categories
        for (String i : categoriesString) {
            //Set chip style to chip_choice
            ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(getContext(), null, 0, R.style.Widget_MaterialComponents_Chip_Choice);
            //create chip
            Chip chip = new Chip(getContext());
            chip.setChipDrawable(chipDrawable);
            //Set id and text
            chip.setId(ViewCompat.generateViewId());
            chip.setText(i);
            //Set onClickListener
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Check if chip was already clicked
                    if (!chip.isChecked()) {
                        selectedCategoryString = "";
                    } else {
                        //Get index of category
                        int index = 0;
                        if (categoriesString.contains(i)) {
                            index = categoriesString.indexOf(i);
                            selectedCategoryString = i;
                            //Set selected category
                            selectedCategory = categories.get(index);
                        }
                    }
                    //call get expenses to re-run query
                    getIncomes();
                }
            });
            chipGroup.addView(chip);
        }
    }

    private void getIncomes() {
        //Start query with specified class
        ParseQuery<Register> query = ParseQuery.getQuery(Register.class);
        //limit to 20 items
        query.setLimit(20);
        query.include(Register.KEY_CATEGORY);
        //Check if there is a selected category to filter
        if (selectedCategoryString != "") {
            query.whereEqualTo(Register.KEY_CATEGORY,selectedCategory);
        }
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.whereEqualTo("type", true);
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
}