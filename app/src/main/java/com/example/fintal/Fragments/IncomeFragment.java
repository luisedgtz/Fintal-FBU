package com.example.fintal.Fragments;

import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.fintal.Adapters.RegisterAdapter;
import com.example.fintal.EndlessRecyclerViewScrollListener;
import com.example.fintal.MainActivity;
import com.example.fintal.Models.Category;
import com.example.fintal.Models.Register;
import com.example.fintal.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class IncomeFragment extends Fragment {
    public static final String TAG = "IncomeFragment";

    private RecyclerView rvIncomes;
    protected RegisterAdapter adapter;
    protected List<Register> incomesList;
    protected FloatingActionButton btnAddIncome;

    private SwipeRefreshLayout swipeContainer;

    List<Category> categories;
    ArrayList<String> categoriesString;
    String selectedCategoryString;
    Category selectedCategory;

    private EndlessRecyclerViewScrollListener scrollListener;

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
        //Add scroll listener for infinite scrolling
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextData();
            }
        };
        rvIncomes.addOnScrollListener(scrollListener);


        //Define itemTouchHelper for swipe
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        //Attach it to the Recycler View
        itemTouchHelper.attachToRecyclerView(rvIncomes);

        //Get search view and set on query text listener
        SearchView searchView = getView().findViewById(R.id.svIncomes);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        //Set on refresh listener to recycler view
        swipeContainer = getView().findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getIncomes();
            }
        });

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
        ChipGroup chipGroup = getView().findViewById(R.id.chipGroupIncome);
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
                    //call get incomes to re-run query
                    getIncomes();
                }
            });
            chipGroup.addView(chip);
        }
    }

    public void getIncomes() {
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
        //If date selection is not null, set query for month/year
        if (MainActivity.selectedYear != null || MainActivity.selectedMonth != null) {
            Date dateStart = new GregorianCalendar(MainActivity.selectedYear, MainActivity.selectedMonth, 1).getTime();
            Date dateFinish = new GregorianCalendar(MainActivity.selectedYear, MainActivity.selectedMonth + 1, 1).getTime();
            query.whereGreaterThanOrEqualTo("createdAt",dateStart);
            query.whereLessThan("createdAt", dateFinish);
        }
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
                swipeContainer.setRefreshing(false);
            }
        });
    }

    //Method for getting more data after scrolling
    private void loadNextData() {
        //Start query with specified class
        ParseQuery<Register> query = ParseQuery.getQuery(Register.class);
        //limit to 20 items
        query.setLimit(20);
        query.whereLessThan("createdAt", incomesList.get(incomesList.size()-1).getCreatedAt());
        query.include(Register.KEY_CATEGORY);
        //Check if there is a selected category to filter
        if (selectedCategoryString != "") {
            query.whereEqualTo(Register.KEY_CATEGORY,selectedCategory);
        }
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.whereEqualTo("type", true);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        //If date selection is not null, set query for month/year
        if (MainActivity.selectedYear != null || MainActivity.selectedMonth != null) {
            Date dateStart = new GregorianCalendar(MainActivity.selectedYear, MainActivity.selectedMonth, 1).getTime();
            Date dateFinish = new GregorianCalendar(MainActivity.selectedYear, MainActivity.selectedMonth + 1, 1).getTime();
            query.whereGreaterThanOrEqualTo("createdAt",dateStart);
            query.whereLessThan("createdAt", dateFinish);
        }
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Register>() {
            @Override
            public void done(List<Register> objects, ParseException e) {
                //check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                List<Register> newRegisters = objects;
                adapter.addAll(objects);
            }
        });
    }

    private void changeBalance(Number amount) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    Number income = object.getNumber("totalIncome");
                    income = income.floatValue() + amount.floatValue();
                    object.put("totalIncome", income);
                    object.saveInBackground();
                }
            }
        });
    }

    //Simple callback touch helper for swipe to delete on recycler view items
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        //Method for drag and drop, it will not be used for this app but it is necessary to define it
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }
        //Method for swipe functionality
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            //Get position for swiped view holder
            final int position = viewHolder.getAdapterPosition();
            switch (direction) {
                case ItemTouchHelper.LEFT:
                    Log.d(TAG, "Swiped left");
                    Register register = incomesList.get(position);
                    register.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.d(TAG, "delete failed");
                                return;
                            }
                            changeBalance(-register.getAmount().floatValue());
                            incomesList.remove(position);
                            adapter.notifyItemRemoved(position);
                            getIncomes();
                        }
                    });
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.red_expenses))
                    .addSwipeLeftActionIcon(R.drawable.ic_round_delete_24)
                    .create()
                    .decorate();
        }
    };
}