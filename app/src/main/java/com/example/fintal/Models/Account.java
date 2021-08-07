package com.example.fintal.Models;

import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

@Parcel(analyze = Account.class)
public class Account {
    public static final String TAG = "AccountModel";

    public String id;
    public String linkId;
    public String institution;
    public Double currentBalance;
    public Double availableBalance;
    public String category;
    public String currency;
    public String accountNumber;
    public Double creditLimit;
    public Double balance;
    public String cuttingDate;
    public String nextPaymentDate;
    public String urlBank;

    public Account() {}

    public static Account fromJson(JSONObject jsonObject) throws JSONException {
        Account account = new Account();
        account.id = jsonObject.getString("id");
        account.linkId = jsonObject.getString("link");
        account.institution = jsonObject.getJSONObject("institution").getString("name");
        account.currentBalance = jsonObject.getJSONObject("balance").getDouble("current");
        account.availableBalance = jsonObject.getJSONObject("balance").getDouble("available");
        account.balance = account.currentBalance;
        account.category = jsonObject.getString("category");
        account.currency = jsonObject.getString("currency");
        String accountNumber = jsonObject.getString("number");
        account.accountNumber = "*" + accountNumber.substring(accountNumber.length() - 4, accountNumber.length());
        if (jsonObject.get("credit_data") != JSONObject.NULL) {
         account.creditLimit = jsonObject.getJSONObject("credit_data").getDouble("credit_limit");
         account.cuttingDate = jsonObject.getJSONObject("credit_data").getString("cutting_date");
         account.balance = account.creditLimit - account.currentBalance;
         account.nextPaymentDate = jsonObject.getJSONObject("credit_data").getString("next_payment_date");
        }
        //Set bank icon
        account.urlBank = getBankLogo(account.institution);
        return account;
    }

    public static List<Account> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Account> accounts = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {
            accounts.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return accounts;
    }

    //Method for retrieving Bank Accounts from Belvo API given a linkId
    private static String getBankLogo(String institution) throws JSONException {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        String apiUrl = "https://sandbox.belvo.com/api/institutions/";
        RequestHeaders headers = new RequestHeaders();
        headers.put("Authorization" , "Basic OWI5NDkxMDctMWYyZS00MTcwLTk0NTUtMTNmOTY1ZGI5MWVmOlY2MFlKWEZsWkI3ak4jb21FY05BWEVNQFhzOUV6dmljNXE1c1haZjdJeGF6NEV2QmxuUmdFMlVRaXZAUGgyOEI=");
        final String[] returnLogo = new String[1];
        //Call Belvo endpoint
        client.get(apiUrl, headers, params, new JsonHttpResponseHandler() {
            @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = json.jsonObject.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                if (jsonArray.getJSONObject(i).getString("name").equals(institution)) {
                                    returnLogo[0] = jsonArray.getJSONObject(i).getString("icon_logo");
                                    Log.d(TAG, returnLogo[0]);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        returnLogo[0] = null;
                    }
                });
        return "https://statics.sandbox.belvo.io/institutions/icon_logos/erebor.svg";
    }
}
