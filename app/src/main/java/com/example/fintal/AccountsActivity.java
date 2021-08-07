package com.example.fintal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fintal.Adapters.AccountAdapter;
import com.example.fintal.Models.Account;
import com.example.fintal.Models.Link;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class AccountsActivity extends AppCompatActivity {
    public static final String TAG = "AccountsActivity";

    private RecyclerView rvBankAccounts;
    protected AccountAdapter adapter;
    private List<Account> accounts;
    private ImageButton btnBack;
    private ImageButton btnAddAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        getBelvoLinks();

        rvBankAccounts = findViewById(R.id.rvBankAccounts);
        //Initialize array that will hold accounts and AccountAdapter
        accounts = new ArrayList<>();
        adapter = new AccountAdapter(this, accounts);
        rvBankAccounts.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvBankAccounts.setLayoutManager(linearLayoutManager);

        //Set on click listener to back button
        btnBack = findViewById(R.id.btnBackAccounts);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Set on click listener to launch add Belvo account
        btnAddAccount = findViewById(R.id.btnAddAccount);
        btnAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddBelvoActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(AccountsActivity.this);
                startActivity(i, options.toBundle());
            }
        });
    }

    //Method to get Belvo Links from Parse Database for current user
    private void getBelvoLinks() {
        ParseQuery<Link> query = new ParseQuery<Link>(Link.class);
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Link>() {
            @Override
            public void done(List<Link> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue getting posts", e);
                    return;
                }
                for (Link link : objects) {
                    //Call get accounts with each link
                    try {
                        getBelvoAccounts(link.getLinkId());
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                }
            }
        });
    }

    //Method for retrieving Bank Accounts from Belvo API given a linkId
    private void getBelvoAccounts(String linkId) throws JSONException {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("link" , linkId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("link", linkId);
        RequestHeaders headers = new RequestHeaders();
        headers.put("Authorization" , getString(R.string.BasicAuth));
        String apiUrl = "https://sandbox.belvo.com/api/accounts/";
        //Call Belvo endpoint
        client.post(apiUrl, headers, params, jsonObject.toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                adapter.clear();
                //Get json array
                JSONArray jsonArray = json.jsonArray;
                try {
                    adapter.addAll(Account.fromJsonArray(jsonArray));
                } catch (JSONException e) {
                    Log.e(TAG, "JSON exception" , e);
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, response, throwable);
            }
        });
    }
}