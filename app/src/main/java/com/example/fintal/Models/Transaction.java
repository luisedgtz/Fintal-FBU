package com.example.fintal.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Transaction {
    public static final String TAG = "TransactionModel";
    public Double amount;
    public String description;
    public String valueDate;
    public String type;
    public String currency;

    public static Transaction fromJson(JSONObject jsonObject) throws JSONException {
        Transaction transaction = new Transaction();
        transaction.amount = jsonObject.getDouble("amount");
        transaction.description = jsonObject.getString("description");
        transaction.valueDate = jsonObject.getString("value_date");
        transaction.type = jsonObject.getString("type");
        transaction.currency = jsonObject.getString("currency");

        return transaction;
    }

    public static List<Transaction> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            transactions.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return transactions;
    }
}
