package com.example.fintal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.codepath.asynchttpclient.AsyncHttpClient;
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

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class AccountsActivity extends AppCompatActivity {
    public static final String TAG = "AccountsActivity";
    private String accessToken;
    WebView belvoWebView;

    private RecyclerView rvBankAccounts;
    protected AccountAdapter adapter;
    private List<Account> accounts;

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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvBankAccounts.setLayoutManager(linearLayoutManager);
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
                    getBelvoAccounts(link.getLinkId());
                }
            }
        });
    }

    //Method for retrieving Bank Accounts from Belvo API given a linkId
    private void getBelvoAccounts(String linkId) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("link", linkId);
        //Call fintal heroku server endpoint
        String apiUrl = "https://fintal.herokuapp.com/getAccounts";
        client.get(apiUrl, params, new JsonHttpResponseHandler() {
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

            }
        });
    }

    private void getAccessToken() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        String apiUrl = "https://fintal.herokuapp.com/getAccessToken";
        client.get(apiUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    accessToken = json.jsonObject.getString("access");
                    belvoWebView.loadUrl("https://widget.belvo.io/?access_token=" + accessToken);
                    WebSettings webSettings = belvoWebView.getSettings();
                    webSettings.setJavaScriptEnabled(true);
                    webSettings.setAllowContentAccess(true);
                    webSettings.setDomStorageEnabled(true);
                    webSettings.setUseWideViewPort(true);
                    webSettings.setAppCacheEnabled(true);

                    BelvoWebClient webClient = new BelvoWebClient();
                    belvoWebView.setWebViewClient(webClient);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Failure getting access token", throwable);
            }
        });
    }

    private class BelvoWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("https")) {
                return true;
            } else {
                belvoWebView.stopLoading();
                Uri uri = Uri.parse(url);
                String host = uri.getHost();
                if (host == "success") {
                    String link = uri.getQueryParameter("link");
                    String institution = uri.getQueryParameter("institution");
                    Log.d(TAG, link);
                } else if (host == "exit") {
                    Log.d(TAG, "Exit");
                } else {
                    Log.d(TAG, "error");
                }
                belvoWebView.goBack();
            }
            return false;
        }
    }
}