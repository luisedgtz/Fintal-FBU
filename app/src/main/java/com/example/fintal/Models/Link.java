package com.example.fintal.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Link")
public class Link extends ParseObject {
    public static final String KEY_ID_LINK = "linkId";
    public static final String KEY_USER = "user";
    public static final String KEY_INSTITUTION = "institution";

    public Link() {}

    //GET methods
    public String getLinkId() {
        return getString(KEY_ID_LINK);
    }
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }
    public String getInstitution() {
        return getString(KEY_INSTITUTION);
    }

    //SET methods
    public void setLinkId(String id) {
        put(KEY_ID_LINK, id);
    }
    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }
    public void setInstitution(String institution) {
        put(KEY_INSTITUTION, institution);
    }
}
