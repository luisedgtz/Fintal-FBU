package com.example.fintal.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.Date;

@Parcel(analyze = Register.class)
@ParseClassName("Register")
public class Register extends ParseObject {
    //Keys for class attributes
    public static final String KEY_TYPE = "type";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_PHOTO = "filePhoto";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_USER = "user";
    public static final String KEY_VALUE_DATE = "valueDate";

    public Register() {}

    //GET METHODS
    public Boolean getType() {
        return getBoolean(KEY_TYPE);
    }
    public Number getAmount() {
        return getNumber(KEY_AMOUNT);
    }
    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }
    public ParseFile getPhoto() {
        return getParseFile(KEY_PHOTO);
    }
    public ParseObject getCategory() {
        return getParseObject(KEY_CATEGORY);
    }
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }
    public Date getValueDate() {
        return getDate(KEY_VALUE_DATE);
    }

    //SET METHODS
    public void setType(Boolean type) {
        put(KEY_TYPE, type);
    }
    public void setAmount(Number amount) {
        put(KEY_AMOUNT, amount);
    }
    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }
    public void setPhoto(ParseFile filePhoto) {
        put(KEY_PHOTO, filePhoto);
    }
    public void setCategory(ParseObject category) {
        put(KEY_CATEGORY, category);
    }
    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }
    public void setCreatedAt(Date date) {
        put(KEY_VALUE_DATE, date);
    }
}
