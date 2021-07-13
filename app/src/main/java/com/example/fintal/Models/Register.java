package com.example.fintal.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Register")
public class Register extends ParseObject {
    //Keys for class attributes
    public static final String KEY_TYPE = "type";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_PHOTO = "filePhoto";
    public static final String KEY_USER = "user";

    public Register() {}

    //GET METHODS
    public Boolean getType() {
        return getBoolean(KEY_TYPE);
    }
    public Number getAmount() {
        return getNumber(KEY_AMOUNT);
    }
    public ParseFile getPhoto() {
        return getParseFile(KEY_PHOTO);
    }
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    //SET METHODS
    public void setType(Boolean type) {
        put(KEY_TYPE, type);
    }
    public void setAmount(Number amount) {
        put(KEY_AMOUNT, amount);
    }
    public void setPhoto(ParseFile filePhoto) {
        put(KEY_AMOUNT, filePhoto);
    }
    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }
}
