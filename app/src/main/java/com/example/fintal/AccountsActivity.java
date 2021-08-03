package com.example.fintal;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import okhttp3.Headers;

public class AccountsActivity extends AppCompatActivity {
    public static final String TAG = "AccountsActivity";
    private String accessToken;
    WebView belvoWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        belvoWebView = findViewById(R.id.belvoWebview);

        getAccessToken();
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