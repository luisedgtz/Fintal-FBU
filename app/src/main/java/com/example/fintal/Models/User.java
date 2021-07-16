package com.example.fintal.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

@ParseClassName("User")
public class User extends ParseUser {
    public static final String KEY_NAME = "name";
    public static final String KEY_LASTNAME = "lastName";
    public static final String KEY_INCOME = "totalIncome";
    public static final String KEY_EXPENSE = "totalExpenses";
    public static final String KEY_PHOTO = "profilePhoto";

    public User() {}

    //Get methods
    public String getName() {
        return getString(KEY_NAME);
    }
    public String getLastname() {
        return getString(KEY_LASTNAME);
    }
    public Number getTotalIncome() {
        return getNumber(KEY_INCOME);
    }
    public Number getTotalExpenses() {
        return getNumber(KEY_EXPENSE);
    }
    public ParseFile getProfilePhoto() {
        return getParseFile(KEY_PHOTO);
    }

    //Set methods
    public void setName(String name) {
        put(KEY_NAME, name);
    }
    public void setLastname(String lastname) {
        put(KEY_LASTNAME, lastname);
    }
    public void setTotalIncome(Number income) {
        put(KEY_INCOME, income);
    }
    public void setTotalExpenses(Number expenses) {
        put(KEY_EXPENSE, expenses);
    }
    public void setUserPhoto(ParseFile photo) {
        put(KEY_PHOTO, photo);
    }
}
