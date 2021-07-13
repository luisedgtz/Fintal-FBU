package com.example.fintal.Models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

@ParseClassName("User")
public class User extends ParseUser {
    public static final String KEY_NAME = "name";
    public static final String KEY_LASTNAME = "lastName";

    public User() {}

    //Get methods
    public String getName() {
        return getString(KEY_NAME);
    }
    public String getLastname() {
        return getString(KEY_LASTNAME);
    }

    //Set methods
    public void setName(String name) {
        put(KEY_NAME, name);
    }
    public void setLastname(String lastname) {
        put(KEY_LASTNAME, lastname);
    }
}
