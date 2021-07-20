package com.example.fintal.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.fintal.R;

public class NewIncomeFragment extends DialogFragment {
    public static final String TAG = "NewIncomeFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_income, container, false);
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

        //ToDo this array should have the options retrieved from the Parse database for categories
        String[] categories = {"Option1", "Option2", "Option3"};
        //Set adapter for dropdown menu on TextInputLayout
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.list_item, categories);
        AutoCompleteTextView tv = getView().findViewById(R.id.categoryIncome);
        tv.setAdapter(adapter);
    }
}
