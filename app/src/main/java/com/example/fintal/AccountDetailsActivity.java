package com.example.fintal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fintal.Adapters.AccountAdapter;
import com.example.fintal.Adapters.TransactionAdapter;
import com.example.fintal.Models.Account;
import com.example.fintal.Models.Transaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

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
}