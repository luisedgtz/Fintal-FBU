package com.example.fintal.Fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.fintal.Models.Category;
import com.example.fintal.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class NewExpenseFragment extends DialogFragment {
    public static final String TAG = "NewExpenseFragment";
    List<Category> categories;
    ArrayList<String> categoriesString;
    AutoCompleteTextView categoryPicker;
    Button btnSave;
    Button btnCancel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_expense, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }

        //Initialize arrays for categories
        categories = new ArrayList<>();
        categoriesString = new ArrayList<>();
        getCategories();

        //Set adapter for dropdown menu on TextInputLayout
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.list_item, categoriesString);
        categoryPicker = getView().findViewById(R.id.categoryExpense);
        categoryPicker.setAdapter(adapter);

        //Set on click listener to SAVE BUTTON
        btnSave = getView().findViewById(R.id.btnSaveExpense);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExpense();
            }
        });

        //Set on click listener to CANCEL BUTTON
        btnCancel = getView().findViewById(R.id.btnCancelExpense);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void saveExpense() {
        String textCategory = categoryPicker.getEditableText().toString();
        int index = 0;
        if (categoriesString.contains(textCategory)) {
            index = categoriesString.indexOf(textCategory);
        }
    }

    //Function to get categories
    private void getCategories() {
        ParseQuery<Category> query = ParseQuery.getQuery(Category.class);
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
            }
        });
    }
}