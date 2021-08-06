package com.example.fintal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fintal.Adapters.AccountAdapter;
import com.example.fintal.Adapters.TransactionAdapter;
import com.example.fintal.Models.Account;
import com.example.fintal.Models.Register;
import com.example.fintal.Models.Transaction;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import okhttp3.Headers;

public class AccountDetailsActivity extends AppCompatActivity {

    public static final String TAG = "AccountDetailsActivity";

    private ImageButton btnBack;
    private TextView tvAccountNumber;
    private TextView tvBalance;
    private ImageView ivInstitutionLogo;
    private ConstraintLayout clCreditInfo;
    private ProgressBar pbCredit;
    private TextView tvTotalCredit;
    private TextView tvPaymentDate;
    private RecyclerView rvTransactions;
    protected TransactionAdapter adapter;
    private List<Transaction> transactions;

    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        //Get views by id
        tvAccountNumber = findViewById(R.id.tvAccountNumberDetails);
        tvBalance = findViewById(R.id.tvBalanceDetails);
        ivInstitutionLogo = findViewById(R.id.ivBankInstitution);
        clCreditInfo = findViewById(R.id.clCreditInfo);
        pbCredit = findViewById(R.id.pbCredit);
        tvTotalCredit = findViewById(R.id.tvTotalCredit);
        tvPaymentDate = findViewById(R.id.tvPaymentDate);
        rvTransactions = findViewById(R.id.rvBankTransactions);
        //Initialize array that will hold accounts and AccountAdapter
        transactions = new ArrayList<>();
        adapter = new TransactionAdapter(this, transactions);
        rvTransactions.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTransactions.setLayoutManager(linearLayoutManager);

        //Get account from Parceler wrap and set views' content
        account = Parcels.unwrap(getIntent().getParcelableExtra(Account.class.getSimpleName()));
        tvAccountNumber.setText(account.accountNumber);
        String formattedValue = String.format("%.2f", account.balance);
        tvBalance.setText("$ " + formattedValue + " " + account.currency);
        if (account.nextPaymentDate == null) {
            clCreditInfo.setVisibility(View.GONE);
        } else {
            String formattedCredit = String.format("%.2f", account.creditLimit);
            tvTotalCredit.setText(formattedCredit + " " + account.currency);
            tvPaymentDate.setText(account.nextPaymentDate);
            Double progressPercent = account.balance * 100 / account.creditLimit;
            pbCredit.setProgress(progressPercent.intValue(), true);
        }

        btnBack = findViewById(R.id.btnBackAccount);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Call get transactions for current account
        getTransactions(account.id);

        //Define itemTouchHelper for swipe
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        //Attach it to the Recycler View
        itemTouchHelper.attachToRecyclerView(rvTransactions);
    }

    private void getTransactions(String id) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id", id);
        //Call fintal heroku server
        String apiUrl = "https://fintal.herokuapp.com/getTransactions";
        client.get(apiUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                adapter.clear();
                //Get json array
                JSONArray jsonArray = json.jsonArray;
                try {
                    adapter.addAll(Transaction.fromJsonArray(jsonArray));
                } catch (JSONException e) {
                    Log.e(TAG, "JSON exception" , e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Failure getting transactions");
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
                    Transaction transaction = transactions.get(position);
                    Number amount = transaction.amount;
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date date;
                    try {
                        date = format.parse(transaction.valueDate);
                        System.out.println(date);
                    } catch (java.text.ParseException e) {
                        date = null;
                        e.printStackTrace();
                    }

                    //Check if transaction is Income or Expense
                    if (transaction.type.equals("OUTFLOW")) {
                        saveExpense(transaction.description, amount, currentUser, position, date);
                    } else {
                        saveIncome(transaction.description, amount, currentUser, position, date);
                    }
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green_fintal))
                    .addSwipeLeftActionIcon(R.drawable.ic_round_add_24_white)
                    .create()
                    .decorate();
        }
    };


    private void saveIncome(String description, Number amount, ParseUser currentUser, int position, Date date) {
        Register register = new Register();
        register.setType(true);
        register.setUser(currentUser);
        register.setAmount(amount);
        register.setDescription(description);
        register.setCreatedAt(date);
        register.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(getApplicationContext(), "Error while saving", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "error", e);
                    return;
                }
                changeBalanceIncome(amount);
                Toast.makeText(getApplicationContext(), "Saved successfully", Toast.LENGTH_SHORT).show();
                adapter.notifyItemChanged(position);
            }
        });
    }

    private void saveExpense(String description, Number amount, ParseUser currentUser, int position, Date date) {
        Register register = new Register();
        register.setType(false);
        register.setUser(currentUser);
        register.setAmount(amount);
        register.setDescription(description);
        register.setCreatedAt(date);
        register.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(getApplicationContext(), "Error while saving", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "error", e);
                    return;
                }
                changeBalanceExpense(amount);
                Toast.makeText(getApplicationContext(), "Saved successfully", Toast.LENGTH_SHORT).show();
                adapter.notifyItemChanged(position);
            }
        });
    }

    private void changeBalanceExpense(Number amount) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    Number expenses = object.getNumber("totalExpenses");
                    expenses = expenses.floatValue() + amount.floatValue();
                    object.put("totalExpenses", expenses);
                    object.saveInBackground();
                }
            }
        });
    }

    private void changeBalanceIncome(Number amount) {
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
}