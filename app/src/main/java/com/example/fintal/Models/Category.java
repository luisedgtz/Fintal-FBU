package com.example.fintal.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Category")
public class Category extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_ICON = "iconFile";

    public Category() {}

    //GET METHODS
    public String getName() {
        return getString(KEY_NAME);
    }
    public ParseFile getIcon() {
        return getParseFile(KEY_ICON);
    }

    //SET METHODS
    public void setName(String name) {
        put(KEY_NAME, name);
    }
    public void setIcon(ParseFile iconFile) {
        put(KEY_ICON, iconFile);
    }
}
