package com.example.fintal.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

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
        return account;
    }

    public static List<Account> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Account> accounts = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {
            accounts.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return accounts;
    }
}
