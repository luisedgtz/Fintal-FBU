package com.example.fintal;

import android.app.Application;

import com.example.fintal.Models.Register;
import com.example.fintal.Models.User;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Register parse models
        ParseObject.registerSubclass(Register.class);
        ParseObject.registerSubclass(User.class);

        //Initialize parse application
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );
    }
}
