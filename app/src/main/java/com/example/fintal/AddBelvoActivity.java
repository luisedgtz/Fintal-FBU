package com.example.fintal;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fintal.Models.Link;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;

import okhttp3.Headers;

public class AddBelvoActivity extends AppCompatActivity {
    public static final String TAG = "BelvoActivity";

    private String accessToken;
    private WebView belvoWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_belvo);

        belvoWebView = findViewById(R.id.webView);
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
                Log.d(TAG, "HostMessage " + host);
                if (host.equals("success")) {
                    String link = uri.getQueryParameter("link");
                    String institution = uri.getQueryParameter("institution");
                    Log.d(TAG, link);
                    Link bankLink = new Link();
                    bankLink.setInstitution(institution);
                    bankLink.setLinkId(link);
                    bankLink.setUser(ParseUser.getCurrentUser());
                    bankLink.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Toast.makeText(getApplicationContext(), "Error while saving", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "error", e);
                                return;
                            }
                            Toast.makeText(getApplicationContext(), "Saved successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } else if (host.equals("exit")) {
                    Log.d(TAG, "Exit");
                    finish();
                } else {
                    Log.d(TAG, "error");
                    finish();
                }
                belvoWebView.goBack();
            }
            return false;
        }
    }
}