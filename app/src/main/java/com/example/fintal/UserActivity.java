package com.example.fintal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class UserActivity extends AppCompatActivity {
    public static final String TAG = "UserActivity";

    private ImageButton btnBack;
    private Button btnLogout;
    private ImageView ivUserPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        //Wait for transition to load async data of user profile photo
        postponeEnterTransition();

        ivUserPhoto = findViewById(R.id.ivUserPhoto);
        String objectId = ParseUser.getCurrentUser().getObjectId();
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.include("profilePhoto");
        query.whereEqualTo("objectId" , objectId);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                ParseFile imageUser = objects.get(0).getParseFile("profilePhoto");
                if (imageUser != null) {
                    Log.d(TAG, "Setting photo");
                    //Set photo on ivUserPhoto
                    Glide.with(getApplicationContext()).load(imageUser.getUrl()).circleCrop().into(ivUserPhoto);
                    supportPostponeEnterTransition();
                    //After photo is loaded, transition animation is resumed
                    ivUserPhoto.getViewTreeObserver().addOnPreDrawListener(
                            new ViewTreeObserver.OnPreDrawListener() {
                                @Override
                                public boolean onPreDraw() {
                                    ivUserPhoto.getViewTreeObserver().removeOnPreDrawListener(this);
                                    startPostponedEnterTransition();
                                    return true;
                                }
                            }
                    );
                }
            }
        });

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
            }
        });

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                if (ParseUser.getCurrentUser() == null) {
                    goSignUp();
                }
            }
        });
    }

    private void goSignUp() {
        Intent i = new Intent(this, SignupActivity.class);
        startActivity(i);
        finish();
    }
}